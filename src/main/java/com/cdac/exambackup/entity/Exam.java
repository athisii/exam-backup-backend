package com.cdac.exambackup.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * @author athisii
 * @version 1.0
 * @since 5/5/24
 */

@Entity
@Table(
        uniqueConstraints = {
        @UniqueConstraint(columnNames = {"exam_centre_id", "exam_date_id"})
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Exam extends AuditModel {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    ExamCentre examCentre;

    @NotNull
    @ManyToOne
    ExamDate examDate;
}
