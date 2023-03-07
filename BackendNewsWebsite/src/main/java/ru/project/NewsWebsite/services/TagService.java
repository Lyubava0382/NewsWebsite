package ru.project.NewsWebsite.services;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.project.NewsWebsite.dto.TagDTO;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.models.Post;
import ru.project.NewsWebsite.models.Tag;
import ru.project.NewsWebsite.repositories.TagRepository;
import ru.project.NewsWebsite.util.TagNotFoundException;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    // Поиск в БД тэга по его тексту
    public Tag findOne(String text) {
        Optional<Tag> foundTag = tagRepository.findByText(text);
        return foundTag.orElseThrow(TagNotFoundException::new);
    }

    // Поиск в БД тэга по его тексту или его создание
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

    // Сохранить в БД предпочтение пользователя, относящееся к данному тэгу
    @Transactional
    public void savePerson(Tag tag, TagDTO tagDTO, Person person){
        if (tagDTO.getKindOf().equals("like")){
            if (person.getTags() == null) {
                person.setTags(new ArrayList<Tag>());
            }
                person.getTags().add(tag);
            if (tag.getNoting() == null) {
                tag.setNoting(new ArrayList<Person>());
            }
            tag.getNoting().add(person);
        }
        else if (tagDTO.getKindOf().equals("ban")){
            if (person.getBanTags() == null) {
                person.setBanTags(new ArrayList<Tag>());
            }
            person.getBanTags().add(tag);
            if (tag.getRefuses() == null) {
                tag.setRefuses(new ArrayList<Person>());
            }
            tag.getRefuses().add(person);
        }
        else throw new IllegalArgumentException();
    }

    // Удалить из БД предпочтение пользователя, относящееся к данному тэгу
    @Transactional
    public void deletePerson(Tag tag, TagDTO tagDTO, Person person){
        if (tagDTO.getKindOf().equals("like")){
            if (tag.getNoting().contains(person)) tag.getNoting().remove(person);
            if (person.getTags().contains(tag)) person.getTags().remove(tag);
        }
        else if (tagDTO.getKindOf().equals("ban")){
            if (tag.getRefuses().contains(person)) tag.getRefuses().remove(person);
            if (person.getBanTags().contains(tag)) person.getBanTags().remove(tag);
        }
        else throw new IllegalArgumentException();
    }

    // Сохранить в БД тему статьи
    @Transactional
    public void savePost(Tag tag, Post post){
        if (post.getTags() == null) {
            post.setTags(new ArrayList<Tag>());
        }
        post.getTags().add(tag);
        if (tag.getMarked() == null) {
            tag.setMarked(new ArrayList<Post>());
        }
        tag.getMarked().add(post);
    }

    // Удалить из БД тему статьи
    @Transactional
    public void deletePost(Tag tag, Post post){
        if (tag.getMarked().contains(post)) tag.getMarked().remove(post);
        if (post.getTags().contains(tag)) post.getTags().remove(tag);
    }
}
