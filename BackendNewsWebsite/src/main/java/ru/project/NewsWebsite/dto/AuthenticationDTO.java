package ru.project.NewsWebsite.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class AuthenticationDTO {
    @NotEmpty(message = "Username shouldn't be empty")
    @Size(min = 2, max = 100, message = "Username should be between 2 and 30 characters")
    @Email
    private String username; // Имя пользователя - формат Email

    private String password; // Пароль - кодируется Bcrypt

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}