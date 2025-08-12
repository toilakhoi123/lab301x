package com.khoi.lab.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Email Sender service
 */
@Service
public class EmailSenderService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("khoilmfx28754@funix.edu.vn");
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);

        mailSender.send(message);
        System.out.println("| [service:EmailSenderService] Mail sent to: " + toEmail);
    }
}
