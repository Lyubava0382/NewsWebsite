package ru.project.NewsWebsite.services;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.project.NewsWebsite.dto.PostDTO;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.models.Post;
import ru.project.NewsWebsite.repositories.PostRepository;
import ru.project.NewsWebsite.util.PostNotFoundException;

import static java.time.temporal.ChronoUnit.HOURS;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Neil Alishev
 */
@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository){
        this.postRepository = postRepository;
    }

    public List<Post> findPage() {
        return postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, 3));
    }

    public List<Post> findAll() {
        List<Post> news = postRepository.findAllByOrderByCreatedAtDesc();
        for(Post post : news){
            if (HOURS.between(LocalDateTime.now(), post.getCreatedAt()) > 24)
                news.remove(post);
        }
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
    public void like(Post post, Person person) {
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
