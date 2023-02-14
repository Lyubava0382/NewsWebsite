package ru.project.NewsWebsite.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class PostDTO {
    @NotEmpty(message = "Title shouldn't be empty")
    @Size(min = 2, max = 255, message = "Title should be between 2 and 255 characters")
    private String title;

    @NotEmpty(message = "Text shouldn't be empty")
    private String text;

    private LocalDateTime createdAt;

    private int likes = 0;

    private boolean personLike = false;

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

}
