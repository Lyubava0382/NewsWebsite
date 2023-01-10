package ru.project.NewsWebsite.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.project.NewsWebsite.dto.PostDTO;
import ru.project.NewsWebsite.models.Post;
import ru.project.NewsWebsite.models.Tag;
import ru.project.NewsWebsite.repositories.PeopleRepository;
import ru.project.NewsWebsite.services.PeopleService;
import ru.project.NewsWebsite.services.PostService;
import ru.project.NewsWebsite.services.TagService;
import ru.project.NewsWebsite.util.PostErrorResponse;
import ru.project.NewsWebsite.util.PostNotFoundException;

import java.util.stream.Collectors;

import javax.validation.Valid;
import java.util.List;

@RestController // @Controller + @ResponseBody над каждым методом
@RequestMapping("/news")
@ResponseBody
public class NewsController {

    private final PostService postService;
    private final PeopleService peopleService;
    private final TagService tagService;
    private final ModelMapper modelMapper;

    @Autowired
    public NewsController(PostService postService, PeopleService peopleService, ModelMapper modelMapper,
                          PeopleRepository peopleRepository, TagService tagService) {
        this.peopleService = peopleService;
        this.modelMapper = modelMapper;
        this.postService = postService;
        this.tagService = tagService;
    }

    @GetMapping("/{person_id}")
    public List<PostDTO> getNews(@PathVariable("person_id") int person_id) {
        return postService.findAll(person_id).stream().map(this::convertToPostDTO)
                .collect(Collectors.toList()); // Jackson конвертирует эти объекты в JSON
    }

    @GetMapping("/{person_id}/{post_id}")
    public PostDTO getPost(@PathVariable("person_id") int person_id,
                           @PathVariable("post_id") int post_id) {
        Post post = postService.findOne(post_id);
        PostDTO postDTO = convertToPostDTO(post);
        postService.enrichPostDTO(post, postDTO, peopleService.findOne(person_id));
        return postDTO; // Jackson конвертирует в JSON
    }

    @GetMapping("/{id}/more")
    public PostDTO getPostText(@PathVariable("id") int id) {
        return convertToPostDTOWithText(postService.findOne(id)); // Jackson конвертирует в JSON
    }

    @GetMapping("/{person_id}/{post_id}/like")
    public ResponseEntity<HttpStatus> likePost(@PathVariable("person_id") int person_id,
                                               @PathVariable("post_id") int post_id) {
        postService.like(postService.findOne(post_id), peopleService.findOne(person_id));
        return ResponseEntity.ok(HttpStatus.OK);
        //return convertToPostDTO(postService.findOne(id)); // Jackson конвертирует в JSON
    }

    @DeleteMapping("/{post_id}")
    public ResponseEntity<HttpStatus> deletePostById(@PathVariable("post_id") int id,
                                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors){
                errorMsg.append(error.getField()).append(" - ")
                        .append(error.getDefaultMessage()).append(";");
            }
            throw new PostNotFoundException();
        }
        postService.deletePostById(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{post_id}")
    public ResponseEntity<HttpStatus> changePost(@RequestBody @Valid PostDTO postDTO,
                                                 @PathVariable("post_id") int id,
                                                 BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors){
                errorMsg.append(error.getField()).append(" - ")
                        .append(error.getDefaultMessage()).append(";");
            }
            throw new PostNotFoundException();
        }
        postService.changePost(convertToPost(postDTO, id));
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @ExceptionHandler
    private ResponseEntity<PostErrorResponse> handleException(PostNotFoundException e){
        PostErrorResponse response = new PostErrorResponse(
                "Post with this id wasn't found!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    private PostDTO convertToPostDTO(Post post) {
        PostDTO newpost = convertToPostDTOWithText(post);
        String text = newpost.getText();
        if (text.length() > 100) newpost.setText(text.substring(0, 100));
        return newpost;
    }

    private PostDTO convertToPostDTOWithText(Post post) {
        PostDTO newpost = modelMapper.map(post, PostDTO.class);
        newpost.setLikes(postService.howMuchLikes(post));
        List<String> hashtags = null;
        for (Tag tag : post.getTags()){
            hashtags.add(new StringBuilder(tag.getText()).insert(0, "#").toString());
        }
        newpost.setHashtags(hashtags);
        return newpost;
    }
    private Post convertToPost(PostDTO postDTO, int id) {
        Post post =  modelMapper.map(postDTO, Post.class);
        post.setId(id);
        List<Tag> tags = null;
        for (String hashtag : postDTO.getHashtags()){
            tags.add(tagService.findOne(new StringBuilder(hashtag).delete(0, 0).toString()));
        }
        post.setTags(tags);
        return post;
    }
}
