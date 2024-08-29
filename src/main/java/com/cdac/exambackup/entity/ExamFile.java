package com.cdac.exambackup.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

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
@Schema(description = "Entity which stores information about exam files for the particular exam")
public class ExamFile extends AuditModel {
    /*
        must search by examCentre + examSlot + examDateId + fileType to match for an entry
     */
    @NotNull
    @ManyToOne
    ExamCentre examCentre;

    @NotNull
    @ManyToOne
    Slot slot;

    @NotNull
    @ManyToOne
    FileType fileType;

    @NotNull
    @ManyToOne
    ExamDate examDate;

    /*
     * path for file when saved in this local machine fs.
     * eg. /data/region/exam-centre-code/date/slot/files...
     *
     */
    @Column(nullable = false)
    String filePath = " ";

    @Column(nullable = false)
    Long fileSize = 0L;  // have to compute

    @Column(nullable = false)
    String contentType = " ";

    @Column
    String userUploadedFilename;

    @Column
    String examName;

    @Column
    String labNumber;

    @Transient
    MultipartFile file;
}
