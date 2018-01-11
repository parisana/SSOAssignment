package com.demo.spring_security.domain;

import com.demo.spring_security.CustomValidMatchingPassword;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@CustomValidMatchingPassword
public class UserDto {

    @NotEmpty(message = "First name cannot be empty")
    private String firstName;

    @NotEmpty(message = "First name cannot be empty")
    private String lastName;

    @Email(message = "Provide a valid email")
    private String email;

    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 6, max = 32, message = "Please enter more than 6 characters")
    private String password;

    @NotEmpty
    @Size(min = 6, max = 32)
    private String confirmPassword;
}
