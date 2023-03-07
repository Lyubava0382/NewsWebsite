package ru.project.NewsWebsite.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

public class PersonDTO {
    @NotEmpty(message = "Name shouldn't be empty")
    @Size(min = 2, max = 30, message = "Name should be between 2 and 30 characters")
    private String name; // Имя пользователя

    @NotEmpty(message = "Lastname shouldn't be empty")
    @Size(min = 2, max = 30, message = "Lastname should be between 2 and 30 characters")
    private String lastname; // Фамилия пользователя

    @NotEmpty(message = "Password shouldn't be empty")
    @Size(min = 2, max = 30, message = "Password should be between 2 and 30 characters")
    private String password; // Пароль (кодируется Bcrypt)

    @Email
    @NotEmpty(message = "Email shouldn't be empty")
    private String email; // Электтронная почта пользователя, используется в качестве никнейма

    private List<String> hashtags;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }
}
