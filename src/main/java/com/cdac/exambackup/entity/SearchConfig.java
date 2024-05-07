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
public class SearchConfig extends AuditModel {
    @Column(nullable = false, unique = true, length = 50)
    String entityName;

    @Column(nullable = false)
    // allow search only for String data type. Filter out rest.
    String searchableColumns;  // comma separated column list for the entity type
}
