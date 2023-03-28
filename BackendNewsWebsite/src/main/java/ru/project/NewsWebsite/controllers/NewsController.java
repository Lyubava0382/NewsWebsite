package ru.project.NewsWebsite.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.project.NewsWebsite.dto.PostDTO;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.models.Post;
import ru.project.NewsWebsite.models.Tag;
import ru.project.NewsWebsite.security.PersonDetails;
import ru.project.NewsWebsite.services.PeopleService;
import ru.project.NewsWebsite.services.PostService;
import ru.project.NewsWebsite.util.PostNotCreatedException;
import ru.project.NewsWebsite.util.PostNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.validation.Valid;
import java.util.List;

@RestController // @Controller + @ResponseBody над каждым методом
@RequestMapping("/news")
@ResponseBody
public class NewsController {

    private final PostService postService;
    private final PeopleService peopleService;

    @Autowired
    public NewsController(PostService postService, PeopleService peopleService) {
        this.peopleService = peopleService;
        this.postService = postService;
    }

    // Получить все доступные новости
    @GetMapping()
    public List<PostDTO> getNews() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
            return postService.findAll(peopleService.findEmail(personDetails.getUsername()).getId()).stream().map(postService::convertToPostDTO)
                    .collect(Collectors.toList()); // Jackson конвертирует эти объекты в JSON

        } catch (ClassCastException e) {
            return postService.findAll().stream().map(postService::convertToPostDTO)
                    .collect(Collectors.toList()); // Jackson конвертирует эти объекты в JSON
        }
    }

    //  Получить новость по её id
    @GetMapping("/{post_id}")
    public PostDTO getPost(@PathVariable("post_id") int post_id) {
        Post post = postService.findOne(post_id);
        PostDTO postDTO = postService.convertToPostDTO(post);
        return postDTO; // Jackson конвертирует в JSON
    }

    //  Получить полную новость (в несокращённом виде)
    @GetMapping("/{post_id}/more")
    public PostDTO getPostText(@PathVariable("post_id") int id) {
        return postService.convertToPostDTOWithText(postService.findOne(id)); // Jackson конвертирует в JSON
    }

    //  Поставить новости лайк (доступно авторизованным пользователям)
    @GetMapping("/{post_id}/like")
    public ResponseEntity<HttpStatus> likePost(@PathVariable("post_id") int post_id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = peopleService.findEmail(personDetails.getUsername());
        postService.like(postService.findOne(post_id), person);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    //  Удалить статью (доступно пользователям с правами администратора)
    @DeleteMapping("/admin/{post_id}")
    public ResponseEntity<HttpStatus> deletePostById(@PathVariable("post_id") int id) {
        postService.deletePostById(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    //  Изменить статью (доступно пользователям с правами администратора)
    @PatchMapping("/admin/{post_id}")
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
        postService.changePost(postDTO, id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    //  Создать статью (доступно пользователям с правами администратора)
    @PostMapping("/admin/create")
    public ResponseEntity<HttpStatus> newPost(@RequestBody @Valid PostDTO postDTO,
                                                 BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors){
                errorMsg.append(error.getField()).append(" - ")
                        .append(error.getDefaultMessage()).append(";");
            }
            throw new PostNotCreatedException();
        }
        postService.save(postDTO, 0);
        return ResponseEntity.ok(HttpStatus.OK);
    }


}
