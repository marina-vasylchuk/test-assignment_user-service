package org.mvasylchuk.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.Optional;

@Getter
@AllArgsConstructor
public class UpdateUserRequest {

    @Email
    private String email;
    @Length(min = 1,message = "shouldn't be empty")
    private String firstName;
    @Length(min = 1,message = "shouldn't be empty")
    private String lastName;
    @Past
    private LocalDate birthDate;
    private Optional<String> address;
    private Optional<String> phoneNumber;
}
