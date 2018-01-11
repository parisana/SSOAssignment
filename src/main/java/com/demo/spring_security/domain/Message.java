package com.demo.spring_security.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Calendar;

/**
 * @author Parisana
 */
@Data
@NoArgsConstructor
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotEmpty(message = "Message is required")
    private String text;

    private String summary;

    @Version
    private Calendar created = Calendar.getInstance();

    @OneToOne
    @NotNull
    private User to;

    public Message(Long id, String text, String summary){
        this.id= id;
        this.text= text;
        this.summary = summary;
    }

}
