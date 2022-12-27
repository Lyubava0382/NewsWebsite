package ru.project.NewsWebsite.models;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Post")
public class Post {
    @ManyToMany
    @JoinTable( name = "Likes",
    joinColumns = @JoinColumn(name = "post_id"),
    inverseJoinColumns = @JoinColumn(name = "person_id"))
    private List<Person> liking;

    @OneToMany(mappedBy = "commentator")
    private List<Comment> comments;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Title shouldn't be empty")
    @Size(min = 2, max = 255, message = "Title should be between 2 and 255 characters")
    @Column(name = "title")
    private String title;

    @NotEmpty(message = "Text shouldn't be empty")
    @Column(name = "text")
    private String text;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Post(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public Post() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public List<Person> getLiking() {
        return liking;
    }

    public void setLiking(List<Person> liking) {
        this.liking = liking;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
