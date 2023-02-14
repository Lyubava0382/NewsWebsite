package ru.project.NewsWebsite.services;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.repositories.PeopleRepository;
import ru.project.NewsWebsite.util.PersonNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Neil Alishev
 */
@Service
@Transactional(readOnly = true)
public class PeopleService {

    private final PeopleRepository peopleRepository;
   // private final PasswordEncoder passwordEncoder;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository){ //, PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
       // this.passwordEncoder = passwordEncoder;
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    public Person findOne(int id) {
        Optional<Person> foundPerson = peopleRepository.findById(id);
        return foundPerson.orElseThrow(PersonNotFoundException::new);
    }

    public Person findEmail(String email) {
        Optional<Person> foundPerson = peopleRepository.findByEmail(email);
        return foundPerson.orElseThrow(PersonNotFoundException::new);
    }

    public Person findOneByName(String findName, String findLastname) {
        Optional<Person> foundPerson = peopleRepository.findByNameANDLastname(findName, findLastname);
        return foundPerson.orElseThrow(PersonNotFoundException::new);
    }

    @Transactional
    public void save(Person person){
        enrichPerson(person);
        //person.setPassword(passwordEncoder.encode(person.getPassword()));
        peopleRepository.save(person);
    }

    private void enrichPerson(Person person) {
        person.setCreatedAt(LocalDateTime.now());
    }
}
