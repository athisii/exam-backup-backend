package com.cdac.exambackup.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

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
    @Column(nullable = false, unique = true, length = 20)
    String code;

    @Column(nullable = false)
    String name;

    @ManyToOne // do nothing for the other side of the relation.
    Region region;


    @OneToMany(mappedBy = "examCentre", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ExamFile> examFiles;
}
