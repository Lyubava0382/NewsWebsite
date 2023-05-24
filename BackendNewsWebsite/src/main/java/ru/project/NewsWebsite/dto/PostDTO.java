package ru.project.NewsWebsite.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public class PostDTO {
    @NotEmpty(message = "Title shouldn't be empty")
    @Size(min = 2, max = 255, message = "Title should be between 2 and 255 characters")
    private String title; // Заголовок статьи

    @NotEmpty(message = "Text shouldn't be empty")
    private String text; // Текст статьи

    private List<String> hashtags; // Темы (тэги) статьи

    private LocalDateTime createdAt; // Время создания статьи

    private int likes = 0; // Количество лайков статьи

    private boolean personLike = false; // Лайк от пользователя данной сессии

    @NotEmpty(message = "Photo shouldn't be empty")
    private String picture;  // Фото/картинка, описывающая новость

    private int id;  // Id статьи

    public PostDTO() {
    }

    public PostDTO(String title, String text, String picture, LocalDateTime createdAt) {
        this.title = title;
        this.text = text;
        this.createdAt = createdAt;
        this.picture = picture;
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

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public boolean isPersonLike() {
        return personLike;
    }

    public void setPersonLike(boolean personLike) {
        this.personLike = personLike;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
