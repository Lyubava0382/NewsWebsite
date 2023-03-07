package ru.project.NewsWebsite.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class CommentDTO {

    @NotEmpty(message = "Text shouldn't be empty")
    @Size(min = 2, max = 1000, message = "Text should be between 2 and 1000 characters")
    private String text; // Текст комментария

    private PersonDTO author; // Автор комментария

    private int id; // Id комментария

    private LocalDateTime createdAt; // Время создания комментария

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public PersonDTO getAuthor() {
        return author;
    }

    public void setAuthor(PersonDTO personDTO) {
        this.author = personDTO;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
