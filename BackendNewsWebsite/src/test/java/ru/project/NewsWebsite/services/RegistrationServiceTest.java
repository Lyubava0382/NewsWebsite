package ru.project.NewsWebsite.services;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.repositories.PeopleRepository;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest
class RegistrationServiceTest {
    @Autowired
    private RegistrationService registrationService;
    @MockBean
    private PeopleRepository peopleRepository;
    @Test
    void register() {
        Person person = new Person();
        person.setPassword("password");
        registrationService.register(person);
        Assert.assertNotNull(person.getPassword());
        Assert.assertTrue(CoreMatchers.is(person.getRole()).matches("ROLE_USER"));
        Mockito.verify(peopleRepository, Mockito.times(1)).save(person);
    }
}