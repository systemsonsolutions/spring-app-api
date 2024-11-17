package com.sos.app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sos.app.models.FormData;

@RestController
@RequestMapping("/api/form")
@CrossOrigin(origins = "http://localhost:3000") // Permitir requisições do frontend React
public class FormController {
  @Autowired
  private JavaMailSender mailSender;
  @Value("${spring.mail.username}")
  private String email;

  @PostMapping
  public void handleFormSubmission(@RequestBody FormData formData) {
    sendEmail(formData);
  }

  private void sendEmail(FormData formData) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(formData.getEmail());
    message.setTo(email);
    // message.setCc("israel_alves77@hotmail.com");
    message.setSubject("Form Submission: " + formData.getName());
    message.setText("Name: " + formData.getName() + "\n" +
        "Email: " + formData.getEmail() + "\n" +
        "Message: " + formData.getMessage());
    mailSender.send(message);
  }
}
