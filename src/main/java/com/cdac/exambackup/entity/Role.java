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

    @Column(nullable = false, unique = true)
    Integer code;

    @Column(nullable = false, unique = true)
    String name;
}
