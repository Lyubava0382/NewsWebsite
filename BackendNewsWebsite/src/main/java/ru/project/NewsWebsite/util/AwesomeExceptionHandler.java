package ru.project.NewsWebsite.util;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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

    @ExceptionHandler(CommentNotFoundException.class)
    protected ResponseEntity<AwesomeException> handleCommentNotFoundException() {
        return new ResponseEntity<>(new AwesomeException("Comment not found"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PostNotFoundException.class)
    protected ResponseEntity<AwesomeException> handlePostNotFoundException() {
        return new ResponseEntity<>(new AwesomeException("There is no such post"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PostNotCreatedException.class)
    protected ResponseEntity<AwesomeException> handlePostNotCreatedException() {
        return new ResponseEntity<>(new AwesomeException("Post wasn't created"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TagNotCreatedException.class)
    protected ResponseEntity<AwesomeException> handleTagNotCreatedException() {
        return new ResponseEntity<>(new AwesomeException("Tag wasn't created"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TagNotFoundException.class)
    protected ResponseEntity<AwesomeException> handleTagNotFoundException() {
        return new ResponseEntity<>(new AwesomeException("Tag not found"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<AwesomeException> handleBadCredentialsException() {
        return new ResponseEntity<>(new AwesomeException("Incorrect credentials"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PersonAlreadyExistsException.class)
    protected ResponseEntity<AwesomeException> handlePersonAlreadyExistsException() {
        return new ResponseEntity<>(new AwesomeException("Person with this email already exists"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OutsideAccountException.class)
    protected ResponseEntity<AwesomeException> handleOutsideAccountException() {
        return new ResponseEntity<>(new AwesomeException("You are not logged in to your account"), HttpStatus.BAD_REQUEST);
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