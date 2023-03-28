package ru.project.NewsWebsite.services;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.repositories.PeopleRepository;
import ru.project.NewsWebsite.security.PersonDetails;
import org.hamcrest.MatcherAssert;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest
class PersonDetailsServiceTest {

    @Autowired
    private PersonDetailsService personDetailsService;
    @MockBean
    private PeopleRepository peopleRepository;
    @Test
    void loadUserByUsername(){
        String email = "email@gmail.com";
        Mockito.doReturn(Optional.of(new Person()))
                .when(peopleRepository)
                .findByEmail(email);
        assertThat(personDetailsService.loadUserByUsername(email), instanceOf(PersonDetails.class));
    }

    @Test
    void loadUserByUsernameFail(){
        String email = "email@gmail.com";
        Mockito.doReturn(Optional.empty())
                .when(peopleRepository)
                .findByEmail(email);
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            personDetailsService.loadUserByUsername(email);
        });
        String expectedMessage = "User not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}