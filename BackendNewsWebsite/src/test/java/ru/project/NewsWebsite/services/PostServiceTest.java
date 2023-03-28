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
        PostDTO postDTO = new PostDTO();
        int post_id = 1;
        List<String> hashtags = new ArrayList<>();
        hashtags.add("#hashtags");
        postDTO.setHashtags(hashtags);
        postService.save(postDTO, post_id);
        Mockito.verify(postRepository, Mockito.times(1)).save(any(Post.class));
        Mockito.verify(tagService, Mockito.times(1)).findOrCreateOne("hashtags");
    }

    @Test
    void saveNew() {
        PostDTO postDTO = new PostDTO();
        postService.save(postDTO, 0);
        Mockito.verify(postRepository, Mockito.times(1)).save(any(Post.class));
    }

    @Test
    void convertToPostDTOWithText() {
        Post post = new Post();
        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("tag"));
        post.setTags(tags);
        PostDTO postDTO = postService.convertToPostDTOWithText(post);
        Assert.assertNotNull(postDTO);
        List<String> hashtags = new ArrayList<>();
        hashtags.add("#tag");
        Assert.assertEquals(hashtags,postDTO.getHashtags());

    }

    @Test
    void convertToPost() {
        int post_id = 1;
        PostDTO postDTO = new PostDTO();
        Post post = postService.convertToPost(postDTO, post_id);
        Assert.assertEquals(post_id, post.getId());
        Post new_post = postService.convertToPost(postDTO, 0);
        Assert.assertNotNull(new_post.getCreatedAt());
    }
}