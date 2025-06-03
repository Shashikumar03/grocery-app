package org.example.grocery_app.service;

public interface EmailSenderService {

    public void sendSimpleEmail(String[] toEmails,String body, String subject);
}
