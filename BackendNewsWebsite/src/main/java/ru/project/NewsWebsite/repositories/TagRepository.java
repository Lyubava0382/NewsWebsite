package ru.project.NewsWebsite.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.models.Tag;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    default Optional<Tag> findByText(String text){
        List<Tag> tags = this.findAll();
        for(Tag tag : tags){
            if (Objects.equals(tag.getText(), text)) {
                return Optional.of(tag);
            }
        }
        return Optional.empty();
    }

}