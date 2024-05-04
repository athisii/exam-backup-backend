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

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Region extends AuditModel {
     /*
      Possible Regions (not confirmed)
      *******************
      * CODE  *   NAME  *
      ******************
      *  1    *   NORTH *
      *  2    *   EAST  *
      *  3    *   SOUTH *
      *  4    *   WEST  *
      * *****************
     */

    @NotNull
    @Column(nullable = false, unique = true)
    Integer code;

    @NotBlank
    @Column(nullable = false, unique = true, length = 50)
    String name;
}
