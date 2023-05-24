package ru.project.NewsWebsite.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.project.NewsWebsite.dto.CommentDTO;
import ru.project.NewsWebsite.models.Comment;
import ru.project.NewsWebsite.repositories.CommentsRepository;
import ru.project.NewsWebsite.repositories.PostRepository;
import ru.project.NewsWebsite.security.PersonDetails;
import ru.project.NewsWebsite.util.CommentNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CommentsService {
    private final CommentsRepository commentsRepository;
    private final PostRepository postRepository;
    private final PeopleService peopleService;

    public CommentsService(CommentsRepository commentsRepository, PostRepository postRepository, PeopleService peopleService) {
        this.commentsRepository = commentsRepository;
        this.peopleService = peopleService;
        this.postRepository = postRepository;
    }

    // Сохранить комментарий в БД
    @Transactional
    public void save(Comment comment, CommentDTO commentDTO, int post_id){
        enrichComment(comment, commentDTO, post_id);
        commentsRepository.save(comment);
    }

    // Дополнить сущность комментария
    private void enrichComment(Comment comment, CommentDTO commentDTO, int post_id) {
        comment.setCreatedAt(LocalDateTime.now());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        comment.setCommentator(peopleService.findEmail(personDetails.getUsername()));
        if(postRepository.findById(post_id).isPresent()) {
            comment.setPost(postRepository.findById(post_id).get());
        }
        comment.setText(commentDTO.getText());
    }

    // Вернуть список всех комментариев определённого поста
    public List<Comment> findAll(int post_id) {
        List<Comment> comments = new ArrayList<Comment>(commentsRepository.findAllByOrderByCreatedAtDesc());
        comments.removeIf(comment -> comment.getPost().getId() != post_id);
        return comments;
    }

    // Удалить комментарий из БД
    @Transactional
    public void deleteCommentById(int id){
        Optional<Comment> comment = commentsRepository.findById(id);
        if (!comment.isPresent()) throw new CommentNotFoundException();
        commentsRepository.deleteById(id);
    }
}
