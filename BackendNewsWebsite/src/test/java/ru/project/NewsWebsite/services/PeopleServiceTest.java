package ru.project.NewsWebsite.services;

import org.junit.jupiter.api.Test;
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
import ru.project.NewsWebsite.util.PersonNotFoundException;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest
class PeopleServiceTest {
    @Autowired
    private PeopleService peopleService;
    @MockBean
    private PeopleRepository peopleRepository;

    @Test
    void findOne() {
        int id = 0;
        Mockito.doReturn(Optional.of(new Person()))
                .when(peopleRepository)
                .findById(id);
        assertThat(peopleService.findOne(id), instanceOf(Person.class));
        Mockito.verify(peopleRepository, Mockito.times(1)).findById(id);

    }

    @Test
    void findOneFail() {
        int id = 0;
        Mockito.doReturn(Optional.empty())
                .when(peopleRepository)
                .findById(id);
        assertThrows(PersonNotFoundException.class, () -> {
            peopleService.findOne(id);
        });
        Mockito.verify(peopleRepository, Mockito.times(1)).findById(id);
    }

    @Test
    void findEmail() {
        String email = "email@gmail.com";
        Mockito.doReturn(Optional.of(new Person()))
                .when(peopleRepository)
                .findByEmail(email);
        assertThat(peopleService.findEmail(email), instanceOf(Person.class));
        Mockito.verify(peopleRepository, Mockito.times(1)).findByEmail(email);

    }

    @Test
    void findEmailFail() {
        String email = "email@gmail.com";
        Mockito.doReturn(Optional.empty())
                .when(peopleRepository)
                .findByEmail(email);
        assertThrows(PersonNotFoundException.class, () -> {
            peopleService.findEmail(email);
        });
        Mockito.verify(peopleRepository, Mockito.times(1)).findByEmail(email);
    }
}