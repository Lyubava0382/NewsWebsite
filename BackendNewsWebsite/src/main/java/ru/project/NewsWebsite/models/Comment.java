package ru.project.NewsWebsite.models;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "Comments")
public class Comment {
    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn( name = "person_id", referencedColumnName = "id")
    private Person commentator; // Автор комментария

    @ManyToOne
    @JoinColumn( name = "post_id", referencedColumnName = "id")
    private Post post; // Новость, которой принадлежит комментарий

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Id комментария

    @NotEmpty(message = "Text shouldn't be empty")
    @Size(min = 2, max = 1000, message = "Text should be between 2 and 1000 characters")
    @Column(name = "text")
    private String text; // Текст комментария

    @Column(name = "created_at")
    private LocalDateTime createdAt; // Время создания комментария

    public Person getCommentator() {
        return commentator;
    }

    public void setCommentator(Person commentator) {
        this.commentator = commentator;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

