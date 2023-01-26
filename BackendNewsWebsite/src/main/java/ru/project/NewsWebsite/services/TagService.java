package ru.project.NewsWebsite.services;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.models.Post;
import ru.project.NewsWebsite.models.Tag;
import ru.project.NewsWebsite.repositories.TagRepository;
import ru.project.NewsWebsite.util.PostNotFoundException;
import ru.project.NewsWebsite.util.TagNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tag findOne(String text) {
        Optional<Tag> foundTag = tagRepository.findByText(text);
        return foundTag.orElseThrow(TagNotFoundException::new);
    }

    @Transactional
    public Tag findOrCreateOne(String text) {
        Optional<Tag> foundTag = tagRepository.findByText(text);
        if (foundTag.isEmpty()) {
            Tag tag = new Tag(text);
            tagRepository.save(tag);
            return tag;
        }
        else return foundTag.get();
    }
    @Transactional
    public void savePerson(Tag tag, Person person){
        if (tag.getKindOf().equals("like")){
            person.getTags().add(tag);
            tag.getNoting().add(person);
        }
        else if (tag.getKindOf().equals("ban")){
            person.getBanTags().add(tag);
            tag.getRefuses().add(person);
        }
        else throw new IllegalArgumentException();
    }

    @Transactional
    public void deletePerson(Tag tag, Person person){
        if (tag.getKindOf().equals("like")){
            if (tag.getNoting().contains(person)) tag.getNoting().remove(person);
            if (person.getTags().contains(tag)) person.getTags().remove(tag);
        }
        else if (tag.getKindOf().equals("ban")){
            if (tag.getRefuses().contains(person)) tag.getRefuses().remove(person);
            if (person.getBanTags().contains(tag)) person.getBanTags().remove(tag);
        }
        else throw new IllegalArgumentException();
    }

    @Transactional
    public void savePost(Tag tag, Post post){
        post.getTags().add(tag);
        tag.getMarked().add(post);
    }

    @Transactional
    public void deletePost(Tag tag, Post post){
        if (tag.getMarked().contains(post)) tag.getMarked().remove(post);
        if (post.getTags().contains(tag)) post.getTags().remove(tag);
    }
}
