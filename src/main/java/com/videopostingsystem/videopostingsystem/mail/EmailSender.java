package com.videopostingsystem.videopostingsystem.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailSender {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(EmailSender.class);
    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(String to, String email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject("Confirm your email");
            helper.setFrom("connectifyalerts@gmail.com");
            mailSender.send(mimeMessage);
        }catch (MessagingException e) {
            LOGGER.error("failed to send email", e);
            throw new IllegalStateException("failed to send email");
        }
    }
}
