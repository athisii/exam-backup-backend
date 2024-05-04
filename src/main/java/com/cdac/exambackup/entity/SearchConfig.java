package com.cdac.exambackup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
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
@AllArgsConstructor
@NoArgsConstructor
public class SearchConfig extends AuditModel {
    @NotBlank
    @Column(nullable = false, unique = true, length = 50)
    String entityName;

    @NotBlank
    @Column(nullable = false)
    // allow search only for String data type. Filter out rest.
    String searchableColumns;  // comma separated column list for the entity type
}
