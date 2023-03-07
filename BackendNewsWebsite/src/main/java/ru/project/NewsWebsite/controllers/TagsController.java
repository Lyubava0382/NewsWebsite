package ru.project.NewsWebsite.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.project.NewsWebsite.dto.TagDTO;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.models.Post;
import ru.project.NewsWebsite.models.Tag;
import ru.project.NewsWebsite.security.PersonDetails;
import ru.project.NewsWebsite.services.PeopleService;
import ru.project.NewsWebsite.services.PostService;
import ru.project.NewsWebsite.services.TagService;
import ru.project.NewsWebsite.util.TagNotCreatedException;

import javax.validation.Valid;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class TagsController {
    private final TagService tagsService;
    private final ModelMapper modelMapper;
    private final PeopleService peopleService;
    private final PostService postService;

    public TagsController(TagService tagsService, ModelMapper modelMapper, PeopleService peopleService, PostService postService) {
        this.tagsService = tagsService;
        this.modelMapper = modelMapper;
        this.peopleService = peopleService;
        this.postService = postService;
    }

    // Добавить предпочтения пользователя
    @PostMapping("/person/edit/tags")
    public ResponseEntity<HttpStatus> addPersonTag( @RequestBody @Valid TagDTO tagDTO,
                                                    BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors){
                errorMsg.append(error.getField()).append(" - ")
                        .append(error.getDefaultMessage()).append(";");
            }
            throw new TagNotCreatedException(errorMsg.toString());
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Tag tag = tagsService.findOrCreateOne(convertToTag(tagDTO).getText());
        Person person = peopleService.findEmail(personDetails.getUsername());
        tagsService.savePerson(tag, tagDTO, person);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    // Удалить предпочтения пользователя
    @DeleteMapping("/person/edit/tags")
    public ResponseEntity<HttpStatus> deletePersonTag(@RequestBody @Valid TagDTO tagDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = peopleService.findEmail(personDetails.getUsername());
        tagsService.deletePerson(tagsService.findOne(convertToTag(tagDTO).getText()), tagDTO, person);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    // Изменить тему (тэг) статьи (доступно пользователям с правами администратора)
    @PostMapping("/news/admin/{post_id}/edit/tags")
    public ResponseEntity<HttpStatus> addPostTag(@PathVariable("post_id") int post_id,
                                                      @RequestBody @Valid TagDTO tagDTO,
                                                      BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors){
                errorMsg.append(error.getField()).append(" - ")
                        .append(error.getDefaultMessage()).append(";");
            }
            throw new TagNotCreatedException(errorMsg.toString());
        }
        Tag tag = tagsService.findOrCreateOne(convertToTag(tagDTO).getText());
        Post post = postService.findOne(post_id);
        tagsService.savePost(tag, post);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    // Удалить тему (тэг) статьи (доступно пользователям с правами администратора)
    @DeleteMapping("/news/admin/{post_id}/edit/tags")
    public ResponseEntity<HttpStatus> deletePostTag(@PathVariable("post_id") int post_id,
                                                      @RequestBody @Valid TagDTO tagDTO) {
        Post post = postService.findOne(post_id);
        tagsService.deletePost(tagsService.findOne(convertToTag(tagDTO).getText()), post);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    // Преобразование TagDTO -> Tag
    private Tag convertToTag(TagDTO tagDTO) {
        Tag tag = modelMapper.map(tagDTO, Tag.class);
        String regex = "^#[\\w]*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(tagDTO.getText());
        if (matcher.matches()){
            tag.setText(new StringBuilder(tagDTO.getText()).delete(0, 1).toString());
        }
        else throw new TagNotCreatedException("Hashtags should start with '#'.");
        return tag;
    }
}
