package ru.project.NewsWebsite.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.project.NewsWebsite.dto.AuthenticationDTO;
import ru.project.NewsWebsite.dto.PersonDTO;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.models.Tag;
import ru.project.NewsWebsite.security.JWTUtil;
import ru.project.NewsWebsite.services.RegistrationService;
import ru.project.NewsWebsite.services.TagService;
import ru.project.NewsWebsite.util.PersonNotCreatedException;
import ru.project.NewsWebsite.util.PersonValidator;

import javax.validation.Valid;
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
            this.personValidator = personValidator;
            this.jwtUtil = jwtUtil;
            this.modelMapper = modelMapper;
            this.authenticationManager = authenticationManager;
            this.tagService = tagService;
        }

        @PostMapping("/registration")
        public Map<String, String> performRegistration(@RequestBody @Valid PersonDTO personDTO,
                                                       BindingResult bindingResult) {
            Person person = convertToPerson(personDTO);

            personValidator.validate(person, bindingResult);

            if (bindingResult.hasErrors()) {
                return Map.of("message", "Ошибка!");
            }

            registrationService.register(person);

            String token = jwtUtil.generateToken(person.getEmail());

            return Map.of("jwt-token", token);
        }


        @PostMapping("/login")
        public Map<String, String> performLogin(@RequestBody AuthenticationDTO authenticationDTO) {
            UsernamePasswordAuthenticationToken authInputToken =
                    new UsernamePasswordAuthenticationToken(authenticationDTO.getUsername(),
                            authenticationDTO.getPassword());

            try {
                authenticationManager.authenticate(authInputToken);
            } catch (BadCredentialsException e) {
                return Map.of("message", "Incorrect credentials!");
            }

            String token = jwtUtil.generateToken(authenticationDTO.getUsername());
            //String token = "123";
            return Map.of("jwt-token", token);
        }


        private Person convertToPerson(PersonDTO personDTO) {
            Person person = this.modelMapper.map(personDTO, Person.class);
            List<Tag> tags = null;
            if (personDTO.getHashtags() != null)
                for (String hashtag : personDTO.getHashtags()){
                tags.add(tagService.findOne(new StringBuilder(hashtag).delete(0, 0).toString()));
            }
            person.setTags(tags);
            return person;
        }
    }
