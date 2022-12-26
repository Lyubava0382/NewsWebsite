package ru.project.NewsWebsite.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.project.NewsWebsite.dto.CommentDTO;
import ru.project.NewsWebsite.models.Comment;
import ru.project.NewsWebsite.models.Post;
import ru.project.NewsWebsite.repositories.CommentsRepository;
import ru.project.NewsWebsite.repositories.PeopleRepository;
import ru.project.NewsWebsite.repositories.PostRepository;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.temporal.ChronoUnit.HOURS;

@Service
@Transactional(readOnly = true)
public class CommentsService {
    private final CommentsRepository commentsRepository;
    private final PeopleRepository peopleRepository;
    private final PostRepository postRepository;

    public CommentsService(CommentsRepository commentsRepository,
                           PeopleRepository peopleRepository, PostRepository postRepository) {
        this.commentsRepository = commentsRepository;
        this.peopleRepository = peopleRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public void save(Comment comment, CommentDTO commentDTO){
        enrichComment(comment, commentDTO);
        commentsRepository.save(comment);
    }
    private void enrichComment(Comment comment, CommentDTO commentDTO) {
        comment.setCreatedAt(LocalDateTime.now());
        if(peopleRepository.findByNameANDLastname(commentDTO.getName(), commentDTO.getLastname()).isPresent()) {
            comment.setCommentator(peopleRepository.findByNameANDLastname(commentDTO.getName(), commentDTO.getLastname()).get());
        }
        if(postRepository.findById(commentDTO.getPost_id()).isPresent()) {
            comment.setPost(postRepository.findById(commentDTO.getPost_id()).get());
        }
    }
    public List<Comment> findAll() {
        return commentsRepository.findAllByOrderByCreatedAtDesc();
    }

}
