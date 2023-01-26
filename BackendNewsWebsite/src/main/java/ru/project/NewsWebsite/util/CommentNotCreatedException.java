package ru.project.NewsWebsite.util;

public class CommentNotCreatedException extends RuntimeException{
    public CommentNotCreatedException(String msg) {
        super(msg);
    }
}