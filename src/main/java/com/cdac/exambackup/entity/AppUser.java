package com.cdac.exambackup.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

/**
 * @author athisii
 * @version 1.0
 * @since 5/4/24
 */


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppUser extends AuditModel {
    @Column(nullable = false, unique = true)
    String userId; // exam-centre-code or admin-id

    @Column
    String name;

    String email;

    @Column(length = 14)
    String mobileNumber; // +0918132813456

    @JsonIgnore
    @Column(nullable = false)
    String password;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    LocalDateTime passExpiryDate;

    int tryCounter;

    boolean locked;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    LocalDateTime unlockTime;

    @Hidden
    boolean isFirstLogin = true; // to reset on first login

    @ManyToOne
    Role role;

    @Column(nullable = false)
    Boolean isRegionHead = false;

    Long regionId;
}
