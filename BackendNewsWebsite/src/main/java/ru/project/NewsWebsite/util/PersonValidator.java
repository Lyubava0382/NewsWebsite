package ru.project.NewsWebsite.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.repositories.PeopleRepository;
import ru.project.NewsWebsite.services.PersonDetailsService;

@Component
public class PersonValidator implements Validator {

    private final PersonDetailsService personDetailsService;
    private final PeopleRepository peopleRepository;

    @Autowired
    public PersonValidator(PersonDetailsService personDetailsService, PeopleRepository peopleRepository) {
        this.personDetailsService = personDetailsService;
        this.peopleRepository = peopleRepository;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Person.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Person person = (Person) o;
        if (!peopleRepository.findByEmail(person.getEmail()).isPresent()) return;
        throw new PersonAlreadyExistsException("Person with this email already exists");
    }
}