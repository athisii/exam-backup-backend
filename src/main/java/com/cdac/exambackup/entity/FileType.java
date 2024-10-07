package com.cdac.exambackup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
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

    @Column(nullable = false, unique = true)
    String code; // string is used to add suffix when deleted

    @Column(nullable = false, unique = true)
    String name;

    @NotNull
    @ManyToOne
    FileExtension fileExtension;
}

