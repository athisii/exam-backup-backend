package com.cdac.exambackup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

/**
 * @author athisii
 * @version 1.0
 * @since 5/15/24
 */

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PasswordResetOtp extends AuditModel {
    @Column(unique = true, nullable = false)
    String userId;
    @Column(nullable = false)
    String otp;
    @Column(nullable = false)
    LocalDateTime expiryDate;
}
