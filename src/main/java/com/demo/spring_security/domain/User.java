package com.demo.spring_security.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * @author Parisana
 */
@Data
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty(message = "First name cannot be empty")
    private String firstName;

    private String lastName;

    @Email(message = "Email address is not valid!")
    @NotEmpty(message = "Email cannot be empty")
    @Column(unique = true, nullable = false)
    private String email;

    @NotEmpty(message = "Password cannot be empty")
    private String password;

    public User(String firstName, String lastName, String email, String password){
        this.firstName= firstName;
        this.lastName= lastName;
        this.email = email;
        this.password = password;
    }

    public User(User user) {
        this.email= user.getEmail();
        this.firstName= user.getFirstName();
        this.lastName= user.getLastName();
        this.id= user.getId();
        this.password= user.getPassword();
    }
}
