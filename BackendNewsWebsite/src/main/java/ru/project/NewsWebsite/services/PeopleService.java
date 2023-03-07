package ru.project.NewsWebsite.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.repositories.PeopleRepository;
import ru.project.NewsWebsite.util.PersonNotFoundException;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository){
        this.peopleRepository = peopleRepository;
    }
    // Поиск в БД человека с заданным ID
    public Person findOne(int id) {
        Optional<Person> foundPerson = peopleRepository.findById(id);
        return foundPerson.orElseThrow(PersonNotFoundException::new);
    }

    // Поиск в БД человека с заданным email
    public Person findEmail(String email) {
        Optional<Person> foundPerson = peopleRepository.findByEmail(email);
        return foundPerson.orElseThrow(PersonNotFoundException::new);
    }

}
