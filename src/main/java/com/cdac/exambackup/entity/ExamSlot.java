package com.cdac.exambackup.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"exam_id", "slot_id"})
        })
public class ExamSlot extends AuditModel {
    @NotNull
    @ManyToOne
    Exam exam;

    @NotNull
    @ManyToOne
    Slot slot;
}
