package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author athisii
 * @version 1.0
 * @since 9/13/24
 */

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Async
    @Override
    public void send(MimeMessage message) {
        javaMailSender.send(message);
    }

    @Async
    @Override
    public void send(SimpleMailMessage message) {
        javaMailSender.send(message);
    }
}
