package ru.project.NewsWebsite.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.project.NewsWebsite.dto.AuthenticationDTO;
import ru.project.NewsWebsite.dto.PersonDTO;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.models.Tag;
import ru.project.NewsWebsite.security.JWTUtil;
import ru.project.NewsWebsite.services.RegistrationService;
import ru.project.NewsWebsite.services.TagService;
import ru.project.NewsWebsite.util.PersonValidator;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final PersonValidator personValidator;
    private final JWTUtil jwtUtil;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;
    private final TagService tagService;

    @Autowired
    public AuthController(RegistrationService registrationService, PersonValidator personValidator,
                          JWTUtil jwtUtil, ModelMapper modelMapper, AuthenticationManager authenticationManager, TagService tagService) {
        this.registrationService = registrationService;
        this.jwtUtil = jwtUtil;
        this.personValidator = personValidator;
        this.modelMapper = modelMapper;
        this.authenticationManager = authenticationManager;
        this.tagService = tagService;
    }

    //    Регистрация нового пользователя
    @PostMapping("/registration")
    public Map<String, String> performRegistration(@RequestBody @Valid PersonDTO personDTO,
                                                   BindingResult bindingResult) {
        Person person = convertToPerson(personDTO);

        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors()) {
            return Collections.singletonMap("message", "Registration error");
        }

        registrationService.register(person);

        String token = jwtUtil.generateToken(person.getEmail());

        return Collections.singletonMap("jwt-token", token);
    }

    //  Аутентификация пользователя
    @PostMapping("/login")
    public Map<String, String> performLogin(@RequestBody AuthenticationDTO authenticationDTO) {
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(),
                        authenticationDTO.getPassword());

        try {
            authenticationManager.authenticate(authInputToken);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect credentials");
        }

        String token = jwtUtil.generateToken(authenticationDTO.getUsername());
        return Collections.singletonMap("jwt-token", token);
    }

    //    Преобразование PersonDTO -> Person
    private Person convertToPerson(PersonDTO personDTO) {
        Person person = this.modelMapper.map(personDTO, Person.class);
        System.out.println(personDTO.getEmail());
        List<Tag> tags = null;
        if (personDTO.getHashtags() != null)
            for (String hashtag : personDTO.getHashtags()){
                tags.add(tagService.findOne(new StringBuilder(hashtag).delete(0, 0).toString()));
            }
        person.setTags(tags);
        return person;
    }

    //    Преобразование Person -> PersonDTO
    public PersonDTO convertToPersonDTO(Person person) {
        PersonDTO personDTO = modelMapper.map(person, PersonDTO.class);
        List<String> hashtags = null;
        for (Tag tag : person.getTags()){
            hashtags.add(new StringBuilder(tag.getText()).insert(0, "#").toString());
        }
        personDTO.setHashtags(hashtags);
        return personDTO;
    }
}
