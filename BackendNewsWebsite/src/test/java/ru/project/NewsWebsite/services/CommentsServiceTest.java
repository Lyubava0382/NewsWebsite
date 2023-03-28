package ru.project.NewsWebsite.services;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.project.NewsWebsite.dto.CommentDTO;
import ru.project.NewsWebsite.models.Comment;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.models.Post;
import ru.project.NewsWebsite.repositories.CommentsRepository;
import ru.project.NewsWebsite.util.CommentNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest
class CommentsServiceTest {
    @Autowired
    private CommentsService commentsService;
    @MockBean
    private CommentsRepository commentsRepository;
    @MockBean
    private PeopleService peopleService;

    @Test
    void save() {
        Comment comment = new Comment();
        CommentDTO commentDTO = new CommentDTO();
        int post_id = 0;
        Person person = new Person();
        person.setEmail("example@mail.ru");

        Mockito.doReturn(person)
                .when(peopleService)
                .findEmail(person.getEmail());

        commentsService.save(comment, commentDTO, post_id);
        Assert.assertNotNull(comment.getCreatedAt());
        Assert.assertNull(comment.getCommentator());
        Mockito.verify(commentsRepository, Mockito.times(1)).save(comment);
    }

    @Test
    void findAll() {
        int post_id = 0;
        Comment comment1 = new Comment();
        Post post1 = new Post();
        post1.setId(post_id);
        List<Comment> setComments = new ArrayList<Comment>();
        setComments.add(comment1);
        post1.setComments(setComments);
        comment1.setPost(post1);

        Comment comment2 = new Comment();
        Post post2 = new Post();
        post2.setId(post_id + 1);
        List<Comment> setCommentsSecond = new ArrayList<Comment>();
        setCommentsSecond.add(comment2);
        post2.setComments(setCommentsSecond);
        comment2.setPost(post2);
        setCommentsSecond.add(comment1);

        Mockito.doReturn(setCommentsSecond)
                .when(commentsRepository)
                .findAllByOrderByCreatedAtDesc();
        List<Comment> comments = commentsService.findAll(post_id);
        Assert.assertTrue(comments.contains(comment1));
        Assert.assertFalse(comments.contains(comment2));
        Mockito.verify(commentsRepository, Mockito.times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void deleteCommentById() {
        int comment_id = 0;
        Mockito.doReturn(Optional.of(new Comment()))
                .when(commentsRepository)
                .findById(comment_id);
        commentsService.deleteCommentById(comment_id);
        Mockito.verify(commentsRepository, Mockito.times(1)).findById(comment_id);
    }

    @Test
    void deleteCommentByIdFail() {
        int comment_id = 0;
        Mockito.doReturn(Optional.empty())
                .when(commentsRepository)
                .findById(comment_id);
        assertThrows(CommentNotFoundException.class, () -> {
            commentsService.deleteCommentById(comment_id);
        });
        Mockito.verify(commentsRepository, Mockito.times(1)).findById(comment_id);
    }
}