package ru.project.NewsWebsite.util;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AwesomeExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PersonNotFoundException.class)
    protected ResponseEntity<AwesomeException> handleThereIsNoSuchUserException() {
        return new ResponseEntity<>(new AwesomeException("There is no such user"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PersonNotCreatedException.class)
    protected ResponseEntity<AwesomeException> handlePersonNotCreatedException() {
        return new ResponseEntity<>(new AwesomeException("Person wasn't created"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CommentNotCreatedException.class)
    protected ResponseEntity<AwesomeException> handleCommentNotCreatedException() {
        return new ResponseEntity<>(new AwesomeException("Comment wasn't created"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PostNotFoundException.class)
    protected ResponseEntity<AwesomeException> handlePostNotFoundException() {
        return new ResponseEntity<>(new AwesomeException("There is no such post"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TagNotCreatedException.class)
    protected ResponseEntity<AwesomeException> handleTagNotCreatedException() {
        return new ResponseEntity<>(new AwesomeException("Tag wasn't created"), HttpStatus.NOT_FOUND);
    }

    private static class AwesomeException {
        private String message;

        public AwesomeException(String message) {
            this.message = message;
        }

        public AwesomeException() {
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}