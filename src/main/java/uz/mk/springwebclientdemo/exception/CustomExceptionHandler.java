package uz.mk.springwebclientdemo.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import uz.mk.springwebclientdemo.exception.response.ApiExceptionResponse;

@RestControllerAdvice
public class CustomExceptionHandler {


    private final Log logger = LogFactory.getLog(getClass());

    @ExceptionHandler({CustomBadRequestException.class, CustomServerErrorException.class, CustomNotFoundException.class})
    public ResponseEntity<?> handleCustomException(ResponseStatusException ex) {
        logger.error(ex.getMessage());
        ApiExceptionResponse exceptionResponse = new ApiExceptionResponse(ex.getReason());
        return ResponseEntity.status(ex.getStatus()).body(exceptionResponse);
    }

}
