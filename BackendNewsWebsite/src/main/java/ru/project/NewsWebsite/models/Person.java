package ru.project.NewsWebsite.models;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Person")
public class Person {
    @ManyToMany(mappedBy = "liking")
    private List<Post> liked; // Лайки, оставленные пользователем

    @OneToMany(mappedBy = "commentator", cascade=CascadeType.MERGE)
    private List<Comment> comments; // Комментарии, оставленные пользователем

    @ManyToMany(mappedBy = "noting")
    private List<Tag> tags; // Тэги, предпочитаемые пользователем

    @ManyToMany(mappedBy = "refuses")
    private List<Tag> banTags; // Тэги, отвергнутые пользователем (не показываются в ленте)

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Id пользователя

    @Column(name = "name")
    @NotEmpty(message = "Name shouldn't be empty")
    @Size(min = 2, max = 30, message = "Name should be between 2 and 30 characters")
    private String name; // Имя пользователя

    @Column(name = "lastname")
    @NotEmpty(message = "Lastname shouldn't be empty")
    @Size(min = 2, max = 30, message = "Lastname should be between 2 and 30 characters")
    private String lastname; // Фамилия пользователя

    @Column(name = "role")
    private String role; // Роль пользователя - ROLE_USER/ROLE_ADMIN

    @Column(name = "email")
    @Email
    @NotEmpty(message = "Email shouldn't be empty")
    private String email; // Электронная почта пользователя, используется в качестве username

    @Column(name = "password")
    @NotEmpty(message = "Password shouldn't be empty")
    private String password; // Пароль пользователя (хранится в Bcrypt)


    public Person() {
        this.setTags(new ArrayList<>());
        this.setLiked(new ArrayList<>());
        this.setBanTags(new ArrayList<>());
        this.setComments(new ArrayList<>());
    }

    public Person(String name, String lastname) {
        this.setTags(new ArrayList<>());
        this.setLiked(new ArrayList<>());
        this.setBanTags(new ArrayList<>());
        this.setComments(new ArrayList<>());
        this.name = name;
        this.lastname = lastname;
    }

    public Person(String name, String lastname, String email, String password) {
        this.setTags(new ArrayList<>());
        this.setLiked(new ArrayList<>());
        this.setBanTags(new ArrayList<>());
        this.setComments(new ArrayList<>());
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public List<Post> getLiked() {
        return liked;
    }

    public void setLiked(List<Post> liked) {
        this.liked = liked;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id && Objects.equals(liked, person.liked) && Objects.equals(name, person.name) && Objects.equals(lastname, person.lastname) && Objects.equals(email, person.email) && Objects.equals(password, person.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(liked, id, name, lastname, email, password);
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Tag> getBanTags() {
        return banTags;
    }

    public void setBanTags(List<Tag> banTags) {
        this.banTags = banTags;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
