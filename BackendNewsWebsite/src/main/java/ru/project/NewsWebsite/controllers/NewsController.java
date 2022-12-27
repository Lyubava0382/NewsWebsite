package ru.project.NewsWebsite.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.project.NewsWebsite.dto.PostDTO;
import ru.project.NewsWebsite.models.Post;
import ru.project.NewsWebsite.repositories.PeopleRepository;
import ru.project.NewsWebsite.services.PeopleService;
import ru.project.NewsWebsite.services.PostService;
import ru.project.NewsWebsite.util.*;
import ru.project.NewsWebsite.util.PostErrorResponse;
import ru.project.NewsWebsite.util.PostNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@RestController // @Controller + @ResponseBody над каждым методом
@RequestMapping("/news")
public class NewsController {

    private final PostService postService;
    private final PeopleService peopleService;
    private final ModelMapper modelMapper;
    private final PeopleRepository peopleRepository;

    @Autowired
    public NewsController(PostService postService, PeopleService peopleService, ModelMapper modelMapper,
                          PeopleRepository peopleRepository) {
        this.peopleService = peopleService;
        this.modelMapper = modelMapper;
        this.postService = postService;
        this.peopleRepository = peopleRepository;
    }

    @GetMapping()
    public List<PostDTO> getNews() {
        return postService.findAll().stream().map(this::convertToPostDTO)
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
        return newpost;
    }
    private Post convertToPost(PostDTO postDTO) {
        return modelMapper.map(postDTO, Post.class);
    }
}
