package ru.project.NewsWebsite.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.project.NewsWebsite.dto.CommentDTO;
import ru.project.NewsWebsite.models.Comment;
import ru.project.NewsWebsite.repositories.CommentsRepository;
import ru.project.NewsWebsite.repositories.PeopleRepository;
import ru.project.NewsWebsite.repositories.PostRepository;
import ru.project.NewsWebsite.security.PersonDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Transactional
    public void save(Comment comment, CommentDTO commentDTO, int post_id){
        enrichComment(comment, commentDTO, post_id);
        commentsRepository.save(comment);
    }
    private void enrichComment(Comment comment, CommentDTO commentDTO, int post_id) {
        comment.setCreatedAt(LocalDateTime.now());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        comment.setCommentator(peopleService.findEmail(personDetails.getUsername()));
        if(postRepository.findById(post_id).isPresent()) {
            comment.setPost(postRepository.findById(post_id).get());
        }
        comment.setId(post_id);
    }
    public List<Comment> findAll(int post_id) {
        List<Comment> comments = new ArrayList<Comment>(commentsRepository.findAllByOrderByCreatedAtDesc());
        comments.removeIf(comment -> comment.getPost().getId() != post_id);
        return comments;
    }

    @Transactional
    public void deleteCommentById(int id){
        commentsRepository.deleteById(id);
    }
}
