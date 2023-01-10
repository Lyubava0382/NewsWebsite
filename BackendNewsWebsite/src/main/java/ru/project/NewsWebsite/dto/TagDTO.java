package ru.project.NewsWebsite.dto;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class TagDTO {

    @NotEmpty(message = "Text shouldn't be empty")
    @Size(min = 1, max = 100, message = "Tag should be between 1 and 100 characters")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
