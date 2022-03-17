package uz.mk.springwebclientdemo.exception.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ApiExceptionResponse {
    private String message;
}
