package com.cdac.exambackup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * @author athisii
 * @version 1.0
 * @since 5/5/24
 */

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FileExtension extends AuditModel {
    @Column(nullable = false, unique = true)
    String code;

    @Column(nullable = false, unique = true)
    String name;
}

