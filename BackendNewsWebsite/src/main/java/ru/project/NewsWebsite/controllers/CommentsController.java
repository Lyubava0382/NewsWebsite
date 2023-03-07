package ru.project.NewsWebsite.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.project.NewsWebsite.dto.CommentDTO;
import ru.project.NewsWebsite.models.Comment;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.services.CommentsService;
import ru.project.NewsWebsite.util.CommentNotCreatedException;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController // @Controller + @ResponseBody над каждым методом
@RequestMapping("/news/{post_id}/comments")
@ResponseBody
public class CommentsController {
    private final CommentsService commentsService;
    private final ModelMapper modelMapper;

    private final AuthController authController;


    public CommentsController(CommentsService commentsService, ModelMapper modelMapper, AuthController authController) {
        this.commentsService = commentsService;
        this.modelMapper = modelMapper;
        this.authController = authController;
    }
    //  Получить комментарий по id
    @GetMapping()
    public List<CommentDTO> getNew(@PathVariable("post_id") int post_id) {
        return commentsService.findAll(post_id).stream().map(this::convertToCommentDTO)
                .collect(Collectors.toList()); // Jackson конвертирует эти объекты в JSON
    }

    // Написать комментарий под новостью с заданным id
    @PostMapping("/create")
    public ResponseEntity<HttpStatus> create(@PathVariable("post_id") int post_id,
                                             @RequestBody @Valid CommentDTO commentDTO,
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
        commentsService.save(convertToComment(commentDTO), commentDTO, post_id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
    //  Удалить комментарий (доступно администратору)
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<HttpStatus> deleteCommentById(@PathVariable("id") int id) {
        commentsService.deleteCommentById(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    //    Преобразование CommentDTO -> Comment
    private Comment convertToComment(CommentDTO commentDTO) {
        return modelMapper.map(commentDTO, Comment.class);
    }

    //    Преобразование Comment -> CommentDTO
    private CommentDTO convertToCommentDTO(Comment comment) {
        CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);
        Person commentator = comment.getCommentator();
        commentDTO.setAuthor(authController.convertToPersonDTO(commentator));
        return commentDTO;
    }
}

