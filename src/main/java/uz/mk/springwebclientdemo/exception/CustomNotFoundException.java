package uz.mk.springwebclientdemo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CustomNotFoundException extends ResponseStatusException {
    public CustomNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND,message);
    }
}