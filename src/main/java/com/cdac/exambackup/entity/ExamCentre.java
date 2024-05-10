package com.cdac.exambackup.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(nullable = false, length = 50)
    String name;

    @ManyToOne // do nothing for the other side of the relation.
    Region region;


    @JsonIgnore //  allow only for custom query for performance reason
    @OneToMany(mappedBy = "examCentre", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ExamFile> examFiles;

    @OneToOne(mappedBy = "examCentre", cascade = CascadeType.ALL, orphanRemoval = true)
    User user;
}
