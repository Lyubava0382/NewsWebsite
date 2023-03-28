package ru.project.NewsWebsite.util;

public class PersonAlreadyExistsException extends RuntimeException{
    public PersonAlreadyExistsException(String msg) {
        super(msg);
    }

}
