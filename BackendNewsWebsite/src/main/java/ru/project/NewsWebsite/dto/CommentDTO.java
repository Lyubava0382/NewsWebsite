package ru.project.NewsWebsite.dto;

import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.models.Post;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class CommentDTO {
    private int post_id;

    @NotEmpty(message = "Name shouldn't be empty")
    @Size(min = 2, max = 1000, message = "Name should be between 2 and 1000 characters")
    private String name;

    @NotEmpty(message = "Lastname shouldn't be empty")
    @Size(min = 2, max = 255, message = "Lastname should be between 2 and 255 characters")
    private String lastname;

    @NotEmpty(message = "Text shouldn't be empty")
    @Size(min = 2, max = 1000, message = "Text should be between 2 and 255 characters")
    private String text;

    //private LocalDateTime createdAt;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /*public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
     */

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }
}
