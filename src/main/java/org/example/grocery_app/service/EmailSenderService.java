package org.example.grocery_app.service;

public interface EmailSenderService {

    public void sendSimpleEmail(String toEmail,String body, String subject);
}
