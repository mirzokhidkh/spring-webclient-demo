package uz.mk.springbootwebflux.model.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestBodyDTO {
    private Long id;

    private String name;

    private String department;

    private Double salary;

    private String email;

    private String address;
}

