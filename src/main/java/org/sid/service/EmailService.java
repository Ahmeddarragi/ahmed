package org.sid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envoie un email générique
     */
    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("adarragi559@gmail.com"); // ✅ Ton adresse expéditrice
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            System.out.println("✅ Email envoyé avec succès !");
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }

    /**
     * Envoie un email de réinitialisation de mot de passe avec le lien contenant le token
     */
  
}