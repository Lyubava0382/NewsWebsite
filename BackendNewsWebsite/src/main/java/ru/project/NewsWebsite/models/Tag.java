package ru.project.NewsWebsite.models;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Tags")
public class Tag {

    @ManyToMany
    @JoinTable( name = "Favor_Tags",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"))
    private List<Person> noting; // Список пользователей, которые предпочитают этот тэг

    @ManyToMany
    @JoinTable( name = "Banned",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"))
    private List<Person> refuses; // Список пользователей, у которых этот тэг заблокирован

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable( name = "Themes",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "post_id"))
    private List<Post> marked; // Новости, в которых присутствует тэг

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Id тэга

    @NotEmpty(message = "Text shouldn't be empty")
    @Column(name = "text")
    @Size(min = 1, max = 100, message = "Tag should be between 1 and 100 characters")
    private String text; // Текст тэга


    public Tag() {
        this.setNoting(new ArrayList<>());
        this.setMarked(new ArrayList<>());
        this.setRefuses(new ArrayList<>());
    }

    public Tag(String text) {
        this.setNoting(new ArrayList<>());
        this.setMarked(new ArrayList<>());
        this.setRefuses(new ArrayList<>());
        this.text = text;
    }

    public List<Person> getNoting() {
        return noting;
    }

    public void setNoting(List<Person> noting) {
        this.noting = noting;
    }

    public List<Post> getMarked() {
        return marked;
    }

    public void setMarked(List<Post> marked) {
        this.marked = marked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Person> getRefuses() {
        return refuses;
    }

    public void setRefuses(List<Person> refuses) {
        this.refuses = refuses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(text, tag.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

}
