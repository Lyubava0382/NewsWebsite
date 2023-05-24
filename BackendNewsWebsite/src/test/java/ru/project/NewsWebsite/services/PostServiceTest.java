package ru.project.NewsWebsite.services;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.project.NewsWebsite.dto.PostDTO;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.models.Post;
import ru.project.NewsWebsite.models.Tag;
import ru.project.NewsWebsite.repositories.PostRepository;
import ru.project.NewsWebsite.util.PostNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest
class PostServiceTest {
    @Autowired
    private PostService postService;
    @MockBean
    private PostRepository postRepository;
    @MockBean
    private TagService tagService;

    @Test
    void findAll() {
        List<Post> news = new ArrayList<>();
        Post post1 = new Post();
        post1.setCreatedAt(LocalDateTime.now());
        news.add(post1);
        Post post2 = new Post();
        post2.setCreatedAt(LocalDateTime.now().minusDays(2));
        news.add(post2);
        Mockito.doReturn(news)
                .when(postRepository)
                .findAllByOrderByCreatedAtDesc();
        List<Post> posts = postService.findAll();
        Assert.assertTrue(posts.contains(post1));
        Assert.assertFalse(posts.contains(post2));
        Mockito.verify(postRepository, Mockito.times(1)).findAllByOrderByCreatedAtDesc();
    }


    @Test
    void findOne() {
        int post_id = 0;
        Mockito.doReturn(Optional.of(new Post()))
                .when(postRepository)
                .findById(post_id);
        assertThat(postService.findOne(post_id), instanceOf(Post.class));
        Mockito.verify(postRepository, Mockito.times(1)).findById(post_id);
    }

    @Test
    void findOneFail() {
        int post_id = 0;
        Mockito.doReturn(Optional.empty())
                .when(postRepository)
                .findById(post_id);
        assertThrows(PostNotFoundException.class, () -> {
            postService.findOne(post_id);
        });
        Mockito.verify(postRepository, Mockito.times(1)).findById(post_id);
    }

    @Test
    void like() {
        Post post = new Post();
        Person person = new Person();
        postService.like(post, person);
        Assert.assertTrue(post.getLiking().contains(person));
        Assert.assertTrue(person.getLiked().contains(post));
    }

    @Test
    void unlike() {
        Post post = new Post();
        Person person = new Person();
        List<Person> setLiking = new ArrayList<>();
        setLiking.add(person);
        post.setLiking(setLiking);
        List<Post> setLiked = new ArrayList<>();
        setLiked.add(post);
        person.setLiked(setLiked);
        postService.like(post, person);
        Assert.assertFalse(post.getLiking().contains(person));
        Assert.assertFalse(person.getLiked().contains(post));
    }

    @Test
    void deletePostById() {
        int post_id = 0;
        Mockito.doReturn(Optional.of(new Post()))
                .when(postRepository)
                .findById(post_id);
        postService.deletePostById(post_id);
        Mockito.verify(postRepository, Mockito.times(1)).deleteById(post_id);
    }

    @Test
    void deletePostByIdFail() {
        int post_id = 0;
        Mockito.doReturn(Optional.empty())
                .when(postRepository)
                .findById(post_id);
        assertThrows(PostNotFoundException.class, () -> {
            postService.deletePostById(post_id);
        });
        Mockito.verify(postRepository, Mockito.times(0)).deleteById(post_id);
    }

    @Test
    void save() {
        Post post = new Post();
        postService.save(post);
        assertTrue(post.getCreatedAt() != null);
        Mockito.verify(postRepository, Mockito.times(1)).save(any(Post.class));
    }

    @Test
    void saveNew() {
        Post post = new Post();
        postService.save(post);
        Mockito.verify(postRepository, Mockito.times(1)).save(any(Post.class));
    }

}