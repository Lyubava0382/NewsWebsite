package ru.project.NewsWebsite.services;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.project.NewsWebsite.dto.TagDTO;
import ru.project.NewsWebsite.models.Comment;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.models.Post;
import ru.project.NewsWebsite.models.Tag;
import ru.project.NewsWebsite.repositories.CommentsRepository;
import ru.project.NewsWebsite.repositories.TagRepository;
import ru.project.NewsWebsite.util.CommentNotFoundException;
import ru.project.NewsWebsite.util.TagNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
@RunWith(SpringRunner.class)
@SpringBootTest
class TagServiceTest {

    @Autowired
    private TagService tagService;
    @MockBean
    private TagRepository tagRepository;

    @Test
    void findOne() {
        String text = "tag";
        Mockito.doReturn(Optional.of(new Tag(text)))
                .when(tagRepository)
                .findByText(text);
        Tag tag = tagService.findOne(text);
        Assert.assertEquals(text, tag.getText());
        Mockito.verify(tagRepository, Mockito.times(1)).findByText(text);
    }

    @Test
    void findOneFail() {
        String text = "tag";
        Mockito.doReturn(Optional.empty())
                .when(tagRepository)
                .findByText(text);
        assertThrows(TagNotFoundException.class, () -> {
            tagService.findOne(text);
        });
        Mockito.verify(tagRepository, Mockito.times(1)).findByText(text);
    }

    @Test
    void findOrCreateOne() {
        String text = "tag";
        Mockito.doReturn(Optional.empty())
                .when(tagRepository)
                .findByText(text);
        Tag tag = tagService.findOrCreateOne(text);
        Assert.assertEquals(text, tag.getText());
        Mockito.verify(tagRepository, Mockito.times(1)).save(any(Tag.class));

    }

    @Test
    void savePersonTag() {
        Tag tag = new Tag();
        TagDTO tagDTO = new TagDTO();
        tagDTO.setKindOf("like");
        Person person = new Person();
        tagService.savePerson(tag, tagDTO, person);
        Assert.assertTrue(tag.getNoting().contains(person));
        Assert.assertTrue(person.getTags().contains(tag));
    }

    @Test
    void savePersonBanTag() {
        Tag tag = new Tag();
        TagDTO tagDTO = new TagDTO();
        tagDTO.setKindOf("ban");
        Person person = new Person();
        tagService.savePerson(tag, tagDTO, person);
        Assert.assertTrue(tag.getRefuses().contains(person));
        Assert.assertTrue(person.getBanTags().contains(tag));
    }

    @Test
    void savePersonFail() {
        Tag tag = new Tag();
        TagDTO tagDTO = new TagDTO();
        tagDTO.setKindOf("any");
        Person person = new Person();
        assertThrows(IllegalArgumentException.class, () -> {
            tagService.savePerson(tag, tagDTO, person);
        });
    }

    @Test
    void deletePersonNonTag() {
        Tag tag = new Tag();
        TagDTO tagDTO = new TagDTO();
        tagDTO.setKindOf("like");
        Person person = new Person();
        tagService.deletePerson(tag, tagDTO, person);
        Assert.assertFalse(tag.getNoting().contains(person));
        Assert.assertFalse(person.getTags().contains(tag));
    }

    @Test
    void deletePersonTag() {
        Tag tag = new Tag();
        TagDTO tagDTO = new TagDTO();
        tagDTO.setKindOf("like");
        Person person = new Person();
        person.getTags().add(tag);
        tag.getNoting().add(person);
        Assert.assertTrue(tag.getNoting().contains(person));
        Assert.assertTrue(person.getTags().contains(tag));
        tagService.deletePerson(tag, tagDTO, person);
        Assert.assertFalse(tag.getNoting().contains(person));
        Assert.assertFalse(person.getTags().contains(tag));
    }

    @Test
    void deletePersonBanTag() {
        Tag tag = new Tag();
        TagDTO tagDTO = new TagDTO();
        tagDTO.setKindOf("ban");
        Person person = new Person();
        person.getBanTags().add(tag);
        tag.getRefuses().add(person);
        Assert.assertTrue(tag.getRefuses().contains(person));
        Assert.assertTrue(person.getBanTags().contains(tag));
        tagService.deletePerson(tag, tagDTO, person);
        Assert.assertFalse(tag.getRefuses().contains(person));
        Assert.assertFalse(person.getBanTags().contains(tag));
    }

    @Test
    void deletePersonFail() {
        Tag tag = new Tag();
        TagDTO tagDTO = new TagDTO();
        tagDTO.setKindOf("any");
        Person person = new Person();
        assertThrows(IllegalArgumentException.class, () -> {
            tagService.deletePerson(tag, tagDTO, person);
        });
    }

    @Test
    void savePost() {
        Tag tag = new Tag();
        Post post = new Post();
        tagService.savePost(tag, post);
        Assert.assertTrue(tag.getMarked().contains(post));
        Assert.assertTrue(post.getTags().contains(tag));
    }

    @Test
    void deletePost() {
        Tag tag = new Tag();
        Post post = new Post();
        List<Post> posts = new ArrayList<>();
        posts.add(post);
        tag.setMarked(posts);
        List<Tag> tags = new ArrayList<>();
        tags.add(tag);
        post.setTags(tags);
        tagService.deletePost(tag, post);
        Assert.assertFalse(tag.getMarked().contains(post));
        Assert.assertFalse(post.getTags().contains(tag));
    }
}