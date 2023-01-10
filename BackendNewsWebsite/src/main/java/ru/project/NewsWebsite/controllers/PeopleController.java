package ru.project.NewsWebsite.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.project.NewsWebsite.dto.PersonDTO;
import ru.project.NewsWebsite.models.Person;
import ru.project.NewsWebsite.models.Tag;
import ru.project.NewsWebsite.services.PeopleService;
import ru.project.NewsWebsite.services.TagService;
import ru.project.NewsWebsite.util.PersonErrorResponse;
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

    @GetMapping()
    public List<PersonDTO> getPeople() {
        return peopleService.findAll().stream().map(this::convertToPersonDTO)
                .collect(Collectors.toList()); // Jackson конвертирует эти объекты в JSON
    }

    @GetMapping("/{id}")
    public PersonDTO getPerson(@PathVariable("id") int id) {
        return convertToPersonDTO(peopleService.findOne(id)); // Jackson конвертирует в JSON
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

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid PersonDTO personDTO,
                                             BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors){
                errorMsg.append(error.getField()).append(" - ")
                        .append(error.getDefaultMessage()).append(";");
            }
            throw new PersonNotCreatedException(errorMsg.toString());
        }
        peopleService.save(convertToPerson(personDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }


    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e){
        PersonErrorResponse response = new PersonErrorResponse(
                "Person with this id wasn't found!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException e){
        PersonErrorResponse response = new PersonErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    private Person convertToPerson(PersonDTO personDTO) {
        Person person = modelMapper.map(personDTO, Person.class);
        List<Tag> tags = null;
        for (String hashtag : personDTO.getHashtags()){
            tags.add(tagService.findOne(new StringBuilder(hashtag).delete(0, 0).toString()));
        }
        person.setTags(tags);
        return person;
    }

}