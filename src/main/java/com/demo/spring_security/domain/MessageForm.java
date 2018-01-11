package com.demo.spring_security.domain;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * @author Parisana
 */
public class MessageForm{
    @NotEmpty(message = "Message is required.")
    private String text;

    private String summary;

    @NotEmpty(message = "Email is required.")
    @Email
    private String toEmail;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }
}