package ru.project.NewsWebsite.services;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.project.NewsWebsite.dto.PostDTO;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.models.Post;
import ru.project.NewsWebsite.models.Tag;
import ru.project.NewsWebsite.repositories.PeopleRepository;
import ru.project.NewsWebsite.repositories.PostRepository;
import ru.project.NewsWebsite.security.PersonDetails;
import ru.project.NewsWebsite.util.PostNotFoundException;

import static java.time.temporal.ChronoUnit.HOURS;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Neil Alishev
 */
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

    public List<Post> findPage() {
        return postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 3));
    }

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
                if (numberTags.get(news.get(in)) < numberTags.get(news.get(in + 1))) {
                    Collections.swap(news, in, in + 1 );
                }
            }
        }
    }

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

    public List<Post> findAll(int person_id) {
        Person person = peopleService.findOne(person_id);
        List<Post> news = new ArrayList<Post>(postRepository.findAllByOrderByCreatedAtDesc());
        news.removeIf(post -> HOURS.between(LocalDateTime.now(), post.getCreatedAt()) > 24
        || toBan(post, person));
        sortNews(news, person);
        return news;
    }

    public List<Post> findAll() {
        List<Post> news = new ArrayList<Post>(postRepository.findAllByOrderByCreatedAtDesc());
        news.removeIf(post -> HOURS.between(LocalDateTime.now(), post.getCreatedAt()) > 24);
        return news;
    }

    public Post findOne(int id) {
        Optional<Post> foundPost = postRepository.findById(id);
        return foundPost.orElseThrow(PostNotFoundException::new);
    }

    public boolean findLike(Post post, Person person){
        return postRepository.findLike(post, person);
    }
    @Transactional
    public void like(Post post) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        Person person = peopleService.findEmail(personDetails.getUsername());
        List<Person> newLikePerson = post.getLiking();
        List<Post> newLikePost = person.getLiked();
        if (findLike(post, person)) {
            newLikePerson.remove(person);
            newLikePost.remove(post);
        } else {
            newLikePerson.add(person);
            newLikePost.add(post);
        }
        post.setLiking(newLikePerson);
        person.setLiked(newLikePost);
    }
    public int howMuchLikes(Post post){
        return post.getLiking().size();
    }

    public void enrichPostDTO(Post post, PostDTO postDTO, Person person){
        postDTO.setPersonLike(findLike(post, person));
    }

    @Transactional
    public void deletePostById(int id){
        postRepository.deleteById(id);
    }

    @Transactional
    public void changePost(Post newPost){
        Post post = null;
        if (postRepository.findById(newPost.getId()).isPresent()){
            post = postRepository.findById(newPost.getId()).get();
            System.out.println(post.getId());
            if (!Objects.equals(newPost.getTitle(), post.getTitle())){
                post.setTitle(newPost.getTitle());
            }
            if (!Objects.equals(newPost.getText(), post.getText())){
                post.setText(newPost.getText());
            }
        }
    }
}
