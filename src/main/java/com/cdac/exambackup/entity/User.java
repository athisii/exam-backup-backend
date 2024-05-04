package com.cdac.exambackup.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Date;

/**
 * @author athisii
 * @version 1.0
 * @since 5/4/24
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "app_user")
@Entity
public class User extends AuditModel {
    @NotBlank
    @Column(unique = true, nullable = false, length = 20)
    String userId; // exam-centre-code or admin-id

    @Column(length = 50)
    String name;

    @Column(length = 50)
    String email;

    @Column(length = 14)
    String mobileNumber; // +0918132813456

    @JsonIgnore
    @Column(nullable = false)
    String password;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm a"
    )
    @Temporal(TemporalType.TIMESTAMP)
    Date passExpiryDate;

    int tryCounter;

    boolean locked;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm a"
    )
    @Temporal(TemporalType.TIMESTAMP)
    Date unlockTime;

    boolean firstLogin = true; // to reset on first login

    @ManyToOne
    Role role;
}
