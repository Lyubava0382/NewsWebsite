package ru.project.NewsWebsite.dto;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class TagDTO {

    @NotEmpty(message = "Tag shouldn't be empty")
    @Size(min = 1, max = 100, message = "Tag should be between 1 and 100 characters")
    private String text;

    @NotEmpty(message = "Type shouldn't be empty")
    private String kindOf;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getKindOf() {
        return kindOf;
    }

    public void setKindOf(String kindOf) {
        this.kindOf = kindOf;
    }
}
