package ru.project.NewsWebsite.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.project.NewsWebsite.dto.PersonDTO;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.models.Tag;
import ru.project.NewsWebsite.security.PersonDetails;
import ru.project.NewsWebsite.services.PeopleService;
import ru.project.NewsWebsite.services.TagService;
import ru.project.NewsWebsite.util.PersonNotCreatedException;
import ru.project.NewsWebsite.util.PersonNotFoundException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Neil Alishev
 */
@RestController // @Controller + @ResponseBody над каждым методом
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;
    private final ModelMapper modelMapper;
    private final TagService tagService;

    @Autowired
    public PeopleController(PeopleService peopleService, ModelMapper modelMapper, TagService tagService) {
        this.modelMapper = modelMapper;
        this.peopleService = peopleService;
        this.tagService = tagService;
    }

    /*@GetMapping()
    public List<PersonDTO> getPeople() {
        return peopleService.findAll().stream().map(this::convertToPersonDTO)
                .collect(Collectors.toList()); // Jackson конвертирует эти объекты в JSON
    }

*/

    @GetMapping()
    public PersonDTO getPerson() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
            return convertToPersonDTO(peopleService.findEmail(personDetails.getUsername())); // Jackson конвертирует эти объекты в JSON

        } catch (ClassCastException e) {
            throw new PersonNotFoundException();
        }
    }

    private PersonDTO convertToPersonDTO(Person person) {
        PersonDTO personDTO = modelMapper.map(person, PersonDTO.class);
        List<String> hashtags = null;
        for (Tag tag : person.getTags()){
            hashtags.add(new StringBuilder(tag.getText()).insert(0, "#").toString());
        }
        personDTO.setHashtags(hashtags);
        return personDTO;
    }


}