package ru.project.NewsWebsite.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.project.NewsWebsite.dto.PostDTO;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.models.Post;
import ru.project.NewsWebsite.models.Tag;
import ru.project.NewsWebsite.repositories.PostRepository;
import ru.project.NewsWebsite.security.PersonDetails;
import ru.project.NewsWebsite.util.PostNotFoundException;

import static java.time.temporal.ChronoUnit.HOURS;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PeopleService peopleService;

    private final ModelMapper modelMapper;
    private final TagService tagService;

    @Autowired
    public PostService(PostRepository postRepository, PeopleService peopleService, ModelMapper modelMapper, TagService tagService){
        this.postRepository = postRepository;
        this.peopleService = peopleService;
        this.modelMapper = modelMapper;
        this.tagService = tagService;
    }

    // Сортировка новостей с учётом предпочтений пользователя
    private void sortNews(List<Post> news, Person person){
        HashMap<Post, Integer> numberTags = new HashMap<>();
        for (Post post : news){
            for (Tag tag : person.getTags()){
                for (Tag tag1 : post.getTags()){
                    if (!numberTags.containsKey(post)){
                        numberTags.put(post, 0);
                    }
                    if (tag.getId() == tag1.getId()){
                        numberTags.put(post, numberTags.get(post) + 1);
                    }
                }
            }
        }
        for (int out = news.size() - 1; out >= 1; out--) {
            for (int in = 0; in < news.size() - 1; in++) {
                if (!numberTags.containsKey(news.get(in + 1))) break;
                if (numberTags.get(news.get(in)) == null ||
                        numberTags.get(news.get(in)) < numberTags.get(news.get(in + 1))) {
                    Collections.swap(news, in, in + 1 );
                }
            }
        }
    }

    // Бан новости (статья не показывается в ленте конкретного пользователя)
    private boolean toBan(Post post, Person person){
        for (Tag tag : person.getBanTags()){
            for (Tag tag1 : post.getTags()){
                if (tag.getId() == tag1.getId()){
                    return true;
                }
            }
        }
        return false;
    }

    // Возвращает все новости за последние 24 часа в отсортированном виде с учётом предпочтений пользователя
    public List<Post> findAll(int person_id) {
        Person person = peopleService.findOne(person_id);
        List<Post> news = new ArrayList<Post>(postRepository.findAllByOrderByCreatedAtDesc());
        news.removeIf(post -> HOURS.between(LocalDateTime.now(), post.getCreatedAt()) > 24
        || toBan(post, person));
        sortNews(news, person);
        return news;
    }

    // Возвращает все новости за последние 24 часа для неавторизованного пользователя
    public List<Post> findAll() {
        List<Post> news = new ArrayList<Post>(postRepository.findAllByOrderByCreatedAtDesc());
        news.removeIf(post -> HOURS.between(LocalDateTime.now(), post.getCreatedAt()) > 24);
        return news;
    }

    // Возвращает определённую статью по её ID
    public Post findOne(int id) {
        Optional<Post> foundPost = postRepository.findById(id);
        return foundPost.orElseThrow(PostNotFoundException::new);
    }

    // Поставить лайк новости
    @Transactional
    public void like(Post post) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = peopleService.findEmail(personDetails.getUsername());
        List<Person> newLikePerson = post.getLiking();
        List<Post> newLikePost = person.getLiked();
        if (post.getLiking().contains(person)) {
            newLikePerson.remove(person);
            newLikePost.remove(post);
        } else {
            newLikePerson.add(person);
            newLikePost.add(post);
        }
        post.setLiking(newLikePerson);
        person.setLiked(newLikePost);
    }

    // Возвращает количество лайков определённой статьи
    public int howMuchLikes(Post post){
        return post.getLiking().size();
    }

    // Удалить определённую статью по её ID
    @Transactional
    public void deletePostById(int id){

        try {
            postRepository.deleteById(id);
        }
        catch (EmptyResultDataAccessException e){
            throw new PostNotFoundException();
        }    }

    // Изменить существующую новость
    @Transactional
    public void changePost(PostDTO newPostDTO, int id){
        Post newPost = convertToPost(newPostDTO, id);
        if (postRepository.findById(newPost.getId()).isPresent()){
            Post post = postRepository.findById(newPost.getId()).get();
            if (!Objects.equals(newPost.getTitle(), post.getTitle())){
                post.setTitle(newPost.getTitle());
            }
            if (!Objects.equals(newPost.getText(), post.getText())){
                post.setText(newPost.getText());
            }
            if (!Objects.equals(newPost.getTags(), post.getTags())){
                post.setTags(newPost.getTags());
            }
            if (!Objects.equals(newPost.getPicture(), post.getPicture())){
                post.setPicture(newPost.getPicture());
            }

        }
    }

    // Сохранить новость в БД
    @Transactional
    public void save(PostDTO postDTO, int id){
        Post post = convertToPost(postDTO, id);
        List<Tag> tags = new ArrayList<>();
        if (postDTO.getHashtags() != null) {
            for (String hashtag : postDTO.getHashtags()) {
                String newTag = new StringBuilder(hashtag).delete(0, 1).toString();
                tags.add(tagService.findOrCreateOne(newTag));

            }
        }
        postRepository.save(post);
        for (Tag tag : tags){
            tagService.savePost(tag, post);
        }
    }


    //    Преобразование Post -> PostDTO, где статья обрезается по формату
    public PostDTO convertToPostDTO(Post post) {
        PostDTO newpost = convertToPostDTOWithText(post);
        String text = newpost.getText();
        if (text.length() > 100) newpost.setText(text.substring(0, 400));
        return newpost;
    }

    //    Преобразование Post -> PostDTO, с сохранением полного текста статьи
    public PostDTO convertToPostDTOWithText(Post post) {
        PostDTO newpost = modelMapper.map(post, PostDTO.class);
        newpost.setLikes(this.howMuchLikes(post));
        List<String> hashtags = new ArrayList<>();
        if (!post.getTags().isEmpty()) {
            for (Tag tag : post.getTags()) {
                hashtags.add(new StringBuilder(tag.getText()).insert(0, "#").toString());
            }
        }
        newpost.setHashtags(hashtags);
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
            newpost.setPersonLike(post.getLiking().contains(peopleService.findEmail(personDetails.getUsername())));
        } catch (ClassCastException e) {
        }
        return newpost;
    }

    //    Преобразование PostDTO -> Post
    @Transactional
    public Post convertToPost(PostDTO postDTO, int id) {
        Post post =  modelMapper.map(postDTO, Post.class);
        if (id != 0) post.setId(id);
        else {
            post.setCreatedAt(LocalDateTime.now());
        }
        return post;
    }
}
