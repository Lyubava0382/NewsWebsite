package ru.project.NewsWebsite.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class TagDTO {

    @NotEmpty(message = "Tag shouldn't be empty")
    @Size(min = 1, max = 100, message = "Tag should be between 1 and 100 characters")
    private String text; // Текст тэга, начинается с #

    private String kindOf; // Тип тэга - "like" / "ban"

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
