package ru.project.NewsWebsite.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.repositories.PeopleRepository;

@Service
public class RegistrationService {

    private final PeopleRepository peopleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(PeopleRepository peopleRepository, PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Зарегистрировать нового пользователя (сохранить в БД, зашифровав пароль)
    @Transactional
    public void register(Person person) {
        person.setPassword(passwordEncoder.encode(person.getPassword()));
        if (person.getRole() == null) person.setRole("ROLE_USER");
        if (!peopleRepository.findByEmail(person.getEmail()).isPresent()) peopleRepository.save(person);
    }

}