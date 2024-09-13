package com.cdac.exambackup.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;

/**
 * @author athisii
 * @version 1.0
 * @since 9/13/24
 */

public interface EmailService {
    void send(MimeMessage message);

    void send(SimpleMailMessage message);
}
