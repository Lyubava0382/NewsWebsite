package ru.project.NewsWebsite.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.project.NewsWebsite.models.Comment;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.models.Post;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByOrderByCreatedAtDesc();

    default void deleteComment(int id){
        List <Comment> comments = this.findAll();
        comments.removeIf(comment -> comment.getId() == id);
    }
}