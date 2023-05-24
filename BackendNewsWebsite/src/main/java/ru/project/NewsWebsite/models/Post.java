package ru.project.NewsWebsite.models;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Post")
public class Post {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable( name = "Likes",
    joinColumns = @JoinColumn(name = "post_id"),
    inverseJoinColumns = @JoinColumn(name = "person_id"))
    private List<Person> liking;  // Список лайкнувших новость людей

    @ManyToMany(mappedBy = "marked")
    private List<Tag> tags; // Темы (тэги) статьи

    @OneToMany(mappedBy = "commentator")
    private List<Comment> comments; // Комментарии под новостью

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Id статьи

    @NotEmpty(message = "Title shouldn't be empty")
    @Size(min = 2, max = 255, message = "Title should be between 2 and 255 characters")
    @Column(name = "title")
    private String title; // Заголовок статьи

    @NotEmpty(message = "Text shouldn't be empty")
    @Column(name = "text")
    private String text; // Текст статьи

    @Column(name = "created_at")
    private LocalDateTime createdAt; // Время создания статьи

    @NotEmpty(message = "Photo shouldn't be empty")
    @Column(name = "pictures")
    private String picture; // Фото/картинка, описывающая новость

    public Post(String title, String text, String picture) {
        this.title = title;
        this.text = text;
        this.picture = picture;
        this.setTags(new ArrayList<>());
        this.setLiking(new ArrayList<>());
        this.setComments(new ArrayList<>());
    }

    public Post(String title, String text, String picture, LocalDateTime time) {
        this.title = title;
        this.text = text;
        this.picture = picture;
        this.createdAt = time;
        this.setTags(new ArrayList<>());
        this.setLiking(new ArrayList<>());
        this.setComments(new ArrayList<>());
    }

    public Post() {
        this.setTags(new ArrayList<>());
        this.setLiking(new ArrayList<>());
        this.setComments(new ArrayList<>());
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

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
