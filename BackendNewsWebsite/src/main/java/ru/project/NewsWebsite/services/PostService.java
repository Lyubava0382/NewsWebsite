package ru.project.NewsWebsite.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.models.Post;
import ru.project.NewsWebsite.models.Tag;
import ru.project.NewsWebsite.repositories.PostRepository;
import ru.project.NewsWebsite.util.PostNotFoundException;


import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PeopleService peopleService;

    @Autowired
    public PostService(PostRepository postRepository, PeopleService peopleService){
        this.postRepository = postRepository;
        this.peopleService = peopleService;
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

    // Проверка новости - в бане или доступна
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
        news.removeIf(post -> post.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1))
        || toBan(post, person));
        sortNews(news, person);
        return news;
    }

    // Возвращает все новости за последние 24 часа для неавторизованного пользователя
    public List<Post> findAll() {
        List<Post> news = new ArrayList<>(postRepository.findAllByOrderByCreatedAtDesc());
        news.removeIf(post -> post.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1)));
        return news;
    }

    // Возвращает определённую статью по её ID
    public Post findOne(int id) {
        Optional<Post> foundPost = postRepository.findById(id);
        return foundPost.orElseThrow(PostNotFoundException::new);
    }

    // Поставить лайк новости
    @Transactional
    public void like(Post post, Person person) {
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
        if (!postRepository.findById(id).isPresent()) throw new PostNotFoundException();
        postRepository.deleteById(id);
    }

    // Изменить существующую новость
    @Transactional
    public void changePost(Post newPost){
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
    public void save(Post post){
        if (postRepository.findByTitle(post.getTitle()).isPresent()) {
            return;
        }
        if (post.getCreatedAt() == null)
            post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);

    }



}
