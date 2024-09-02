package com.cdac.exambackup.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

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
                @UniqueConstraint(columnNames = {"start_time", "end_time"})
        })
public class Slot extends AuditModel {
    @Column(nullable = false, unique = true)
    String code; // string is used to add suffix when deleted

    @Column(nullable = false, unique = true)
    String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    LocalTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    LocalTime endTime;
}
