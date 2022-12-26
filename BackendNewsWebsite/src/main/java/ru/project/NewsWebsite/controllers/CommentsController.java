package ru.project.NewsWebsite.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.project.NewsWebsite.dto.CommentDTO;
import ru.project.NewsWebsite.dto.PersonDTO;
import ru.project.NewsWebsite.dto.PostDTO;
import ru.project.NewsWebsite.models.Comment;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.repositories.CommentsRepository;
import ru.project.NewsWebsite.services.CommentsService;
import ru.project.NewsWebsite.services.PeopleService;
import ru.project.NewsWebsite.util.CommentErrorResponse;
import ru.project.NewsWebsite.util.CommentNotCreatedException;
import ru.project.NewsWebsite.util.PersonErrorResponse;
import ru.project.NewsWebsite.util.PersonNotCreatedException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController // @Controller + @ResponseBody над каждым методом
@RequestMapping("/comments")
public class CommentsController {
    private final CommentsService commentsService;
    private final PeopleService peopleService;
    private final ModelMapper modelMapper;
    private final CommentsRepository commentsRepository;

    public CommentsController(CommentsService commentsService, PeopleService peopleService, ModelMapper modelMapper,
                              CommentsRepository commentsRepository) {
        this.commentsService = commentsService;
        this.peopleService = peopleService;
        this.modelMapper = modelMapper;
        this.commentsRepository = commentsRepository;
    }

    @GetMapping()
    public List<CommentDTO> getNews() {
        return commentsService.findAll().stream().map(this::convertToCommentDTO)
                .collect(Collectors.toList()); // Jackson конвертирует эти объекты в JSON
    }
    @PostMapping()
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid CommentDTO commentDTO,
                                             BindingResult bindingResult){
       if (bindingResult.hasErrors()){
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors){
                errorMsg.append(error.getField()).append(" - ")
                        .append(error.getDefaultMessage()).append(";");
            }
            throw new CommentNotCreatedException(errorMsg.toString());
        }
        commentsService.save(convertToComment(commentDTO), commentDTO);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<CommentErrorResponse> handleException(CommentNotCreatedException e){
        CommentErrorResponse response = new CommentErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    private Comment convertToComment(CommentDTO commentDTO) {
        return modelMapper.map(commentDTO, Comment.class);
    }

    private CommentDTO convertToCommentDTO(Comment comment) {
        CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);
        Person commentator = comment.getCommentator();
        System.out.println(commentator.getName());
        commentDTO.setName(commentator.getName());
        commentDTO.setLastname(commentator.getLastname());
        return commentDTO;
    }
}

