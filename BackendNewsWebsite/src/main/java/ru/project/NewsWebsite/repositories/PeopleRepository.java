package ru.project.NewsWebsite.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.project.NewsWebsite.models.Person;

import java.util.Optional;

/**
 * @author Neil Alishev
 */
@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {

    Optional<Person> findByEmail(String email);
}
