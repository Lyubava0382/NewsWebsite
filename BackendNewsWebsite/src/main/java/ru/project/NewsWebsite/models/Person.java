package ru.project.NewsWebsite.models;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @author Neil Alishev
 */
@Entity
@Table(name = "Person")
public class Person {
    @ManyToMany(mappedBy = "liking")
    private List<Post> liked;

    @OneToMany(mappedBy = "commentator")
    private List<Comment> comments;

    @ManyToMany(mappedBy = "noting")
    private List<Tag> tags;

    @ManyToMany(mappedBy = "refuses")
    private List<Tag> banTags;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    @NotEmpty(message = "Name shouldn't be empty")
    @Size(min = 2, max = 30, message = "Name should be between 2 and 30 characters")
    private String name;

    @Column(name = "lastname")
    @NotEmpty(message = "Lastname shouldn't be empty")
    @Size(min = 2, max = 30, message = "Lastname should be between 2 and 30 characters")
    private String lastname;

    @Column(name = "email")
    @Email
    @NotEmpty(message = "Email shouldn't be empty")
    private String email;

    @Column(name = "password")
    @NotEmpty(message = "Password shouldn't be empty")
    @Size(min = 2, max = 30, message = "Password should be between 2 and 30 characters")
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Person() {

    }

    public Person(String name, String lastname) {
        this.name = name;
        this.lastname = lastname;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
        return id == person.id && Objects.equals(liked, person.liked) && Objects.equals(name, person.name) && Objects.equals(lastname, person.lastname) && Objects.equals(email, person.email) && Objects.equals(password, person.password) && Objects.equals(createdAt, person.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(liked, id, name, lastname, email, password, createdAt);
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
}
