package com.cdac.exambackup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * @author athisii
 * @version 1.0
 * @since 5/5/24
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Role extends AuditModel {
    /*
       Role Types
      *************************
      *  CODE   *   NAME      *
      *************************
      *   1    *   ADMIN      *
      *   2    *   STAFF      *
      *   3    *   USER       *
      *   4    *   OTHER      *
      * ***********************
     */

    @NotNull
    @Column(nullable = false, unique = true)
    Integer code;

    @NotBlank
    @Column(nullable = false, unique = true, length = 20)
    String name;
}
