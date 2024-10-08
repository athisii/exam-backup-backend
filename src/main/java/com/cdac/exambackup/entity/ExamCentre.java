package com.cdac.exambackup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
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
public class ExamCentre extends AuditModel {
    @Column(nullable = false, unique = true)
    String code; // string is used to add suffix when deleted

    @Column(nullable = false)
    String name;

    @ManyToOne // do nothing for the other side of the relation.
    Region region;
}
