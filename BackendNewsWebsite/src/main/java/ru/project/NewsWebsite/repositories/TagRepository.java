package ru.project.NewsWebsite.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.project.NewsWebsite.models.Tag;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    Optional<Tag> findByText(String text);

}