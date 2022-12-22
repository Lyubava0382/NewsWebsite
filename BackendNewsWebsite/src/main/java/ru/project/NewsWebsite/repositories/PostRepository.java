package ru.project.NewsWebsite.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.models.Post;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<Post> findAllByOrderByCreatedAtDesc();

    default boolean findLike(Post post, Person person){
        List<Person> liking = post.getLiking();
        for(Person like : liking){
            if (like.equals(person)) return true;
        }
        return false;
    }
}