package com.cdac.exambackup.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

/**
 * @author athisii
 * @version 1.0
 * @since 5/4/24
 */


@Table(name = "app_user")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends AuditModel {
    @Column(nullable = false, unique = true, length = 20)
    String userId; // exam-centre-code or admin-id

    @OneToOne
    ExamCentre examCentre;

    @Column(length = 50)
    String name;

    @Column(length = 50)
    String email;

    @Column(length = 20)
    String mobileNumber; // +0918132813456

    @JsonIgnore
    @Column(nullable = false)
    String password;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a")
    LocalDateTime passExpiryDate;

    int tryCounter;

    boolean locked;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a")
    LocalDateTime unlockTime;

    @Hidden
    boolean firstLogin = true; // to reset on first login

    @ManyToOne
    Role role;
}
