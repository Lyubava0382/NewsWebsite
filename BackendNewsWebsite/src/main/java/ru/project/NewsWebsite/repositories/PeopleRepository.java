package ru.project.NewsWebsite.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.project.NewsWebsite.models.Person;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

/**
 * @author Neil Alishev
 */
@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
    List<Person> findAll();
    Optional<Person> findByEmail(String email);

    default Optional<Person> findByNameANDLastname(String name, String lastname){
        List<Person> people = this.findAll();
        for(Person person : people){
            if (Objects.equals(person.getName(), name) &&
                    Objects.equals(person.getLastname(), lastname)) return Optional.of(person);
        }
        return Optional.empty();
    }
}
