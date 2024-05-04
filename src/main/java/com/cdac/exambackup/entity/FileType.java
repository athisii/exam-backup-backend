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
public class FileType extends AuditModel {
    /*
      Possible File Types (not confirmed)
      ***********************************
      *  CODE   *   NAME                *
      ***********************************
      *   1    *   PXE_LOG              *
      *   2    *   PRIMARY_SERVER_LOG   *
      *   3    *   SECONDARY_SERVER_LOG *
      *   4    *   ATTENDANCE_SHEET     *
      *   5    *   RESPONSE_SHEET       *
      *   6    *   BIOMETRIC_DATA       *
      * *********************************
     */

    @NotNull
    @Column(nullable = false, unique = true)
    Integer code;

    @NotBlank
    @Column(nullable = false, unique = true, length = 50)
    String name;
}

