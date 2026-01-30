package org.example.grocery_app.serviceImplementation;

//import org.example.library.exceptions.ApiException;
//import org.example.library.service.EmailSenderService;
import jakarta.mail.internet.MimeMessage;
import org.example.grocery_app.exception.ApiException;
import org.example.grocery_app.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderServiceImp implements EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;
    @Override
    public void sendSimpleEmail(String[] toEmails, String body, String subject) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("shashiandtechnology3@gmail.com");
//            helper.setTo(toEmail);
            helper.setTo(toEmails); // Accepts array of recipients
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            System.out.println("shashi");
//            throw new ApiException("Invalid email format: " + e.getMessage());
        }
    }

}