package com.cdac.exambackup.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

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
        must search by examCentre + examSlot + examDate + fileType to match for an entry
     */
    @NotNull
    @ManyToOne
    ExamCentre examCentre;

    @NotNull
    @ManyToOne
    ExamSlot examSlot;

    @NotNull
    @ManyToOne // cascade default off
    FileType fileType;

    @NotNull
    @Schema(example = "2024-05-24 11:30 AM")
    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a")
    @Temporal(TemporalType.TIMESTAMP)
    Date examDate;

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

    @Column(length = 50)
    String userUploadedFilename;

    @Column(length = 50)
    String examName;

    @Column(length = 50)
    String labNumber;

    @Transient
    MultipartFile file;
}
