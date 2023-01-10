package ru.project.NewsWebsite.controllers;


import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.project.NewsWebsite.dto.TagDTO;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.models.Post;
import ru.project.NewsWebsite.models.Tag;
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

    @PostMapping("/{person_id}/edit/tags/{kindOf}")
    public ResponseEntity<HttpStatus> addPersonTag(@PathVariable("person_id") int person_id,
                                                      @PathVariable("kindOf") String kindOf,
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
        Person person = peopleService.findOne(person_id);
        if (kindOf.equals("like")) {
            tagsService.savePerson(tag, person);
        }
        else if (kindOf.equals("ban")) {
            tagsService.savePersonBan(tag, person);
        }
        else throw new IllegalArgumentException();
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/{person_id}/edit/tags/{kindOf}")
    public ResponseEntity<HttpStatus> deletePersonTag(@PathVariable("person_id") int person_id,
                                                      @PathVariable("kindOf") String kindOf,
                                                      @RequestBody @Valid TagDTO tagDTO) {
        Person person = peopleService.findOne(person_id);
        if (kindOf.equals("like")) {
            tagsService.deletePerson(tagsService.findOne(convertToTag(tagDTO).getText()), person);
        }
        else if (kindOf.equals("ban")) {
            tagsService.deletePersonBan(tagsService.findOne(convertToTag(tagDTO).getText()), person);
        }
        else throw new IllegalArgumentException();
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/{post_id}/edit/tags")
    public ResponseEntity<HttpStatus> addPersonBanTag(@PathVariable("post_id") int post_id,
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

    @DeleteMapping("/{post_id}/edit/tags")
    public ResponseEntity<HttpStatus> deletePersonTag(@PathVariable("post_id") int post_id,
                                                      @RequestBody @Valid TagDTO tagDTO) {
        Post post = postService.findOne(post_id);
        tagsService.deletePost(tagsService.findOne(convertToTag(tagDTO).getText()), post);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    /*
    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e){
        PersonErrorResponse response = new PersonErrorResponse(
                "Person with this id wasn't found!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException e){
        PersonErrorResponse response = new PersonErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
     */
    private TagDTO convertToTagDTO(Tag tag) {
        TagDTO tagDTO = modelMapper.map(tag, TagDTO.class);
        tagDTO.setText(new StringBuilder(tag.getText()).insert(0, "#").toString());
        return tagDTO;
    }
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
