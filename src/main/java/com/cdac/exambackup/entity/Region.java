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

    @Column(nullable = false, unique = true)
    Integer code;

    @Column(nullable = false, unique = true, length = 50)
    String name;
}
