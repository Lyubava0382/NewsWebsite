package ru.project.NewsWebsite.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.project.NewsWebsite.models.Person;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
    List<Person> findAll();
    Optional<Person> findByEmail(String email);

}
