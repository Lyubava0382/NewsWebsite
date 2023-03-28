package ru.project.NewsWebsite.util;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.BindingResult;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.repositories.PeopleRepository;
import ru.project.NewsWebsite.security.PersonDetails;
import ru.project.NewsWebsite.services.PersonDetailsService;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest
class PersonValidatorTest {
    @Autowired
    private PersonValidator personValidator;
    @MockBean
    private PeopleRepository peopleRepository;
    @MockBean
    private BindingResult bindingResult;

    @Test
    void validate(){
        Person person = new Person();
        person.setEmail("email@gmail.com");
        Mockito.doReturn(Optional.empty())
                .when(peopleRepository)
                .findByEmail("email@gmail.com");
        assertDoesNotThrow(() -> {
            personValidator.validate(person, bindingResult);
        });

    }

    @Test
    void validateFail() {
        Person person = new Person();
        person.setEmail("email@gmail.com");
        Mockito.doReturn(Optional.of(new Person()))
                .when(peopleRepository)
                .findByEmail("email@gmail.com");
        Exception exception = assertThrows(PersonAlreadyExistsException.class, () -> {
            personValidator.validate(person, bindingResult);
        });
        String expectedMessage = "Person with this email already exists";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}