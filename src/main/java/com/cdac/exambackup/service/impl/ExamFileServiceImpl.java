package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.*;
import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.entity.ExamFile;
import com.cdac.exambackup.entity.ExamSlot;
import com.cdac.exambackup.entity.FileType;
import com.cdac.exambackup.exception.GenericException;
import com.cdac.exambackup.service.ExamFileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ExamFileServiceImpl extends AbstractBaseService<ExamFile, Long> implements ExamFileService {
    @Autowired
    ExamFileDao examFileDao;

    @Autowired
    ExamCentreDao examCentreDao;

    @Autowired
    ExamSlotDao examSlotDao;

    @Autowired
    FileTypeDao fileTypeDao;

    public ExamFileServiceImpl(BaseDao<ExamFile, Long> baseDao) {
        super(baseDao);
    }

    @Transactional
    @Override
    public ExamFile save(ExamFile examFileDto) {
        /*
           Data needed from the frontend client:
                    1. exam-centre-id
                    2. exam-slot-id
                    3. file-type-id
                    4. exam-date
                    5. file

           If all required data received:
                1. search with combined ids of examCode, examSlot, fileType, exam-date present in dto to row in a table.
                    if found:
                        overwrite the existing file in fs with the uploaded file.
                    else:
                      save new record
           **********************************************
           If all required data received:
                  1. make directory structure as /data/exam-backup/region/exam-centre-code/date/slot/
                  2. save the file in the above created path.
                  3. save this file path, size, name, etc. in the db.
         */

        if (examFileDto.getExamCentre() == null || examFileDto.getExamSlot() == null || examFileDto.getFileType() == null || examFileDto.getExamDate() == null || examFileDto.getFile() == null || examFileDto.getFile().isEmpty()) {
            throw new GenericException("Please provide all the required data.");
        }
        if (examFileDto.getExamCentre().getId() == null || examFileDto.getExamSlot().getId() == null || examFileDto.getFileType().getId() == null) {
            throw new GenericException("Please provide all the required ids.");
        }

        // check exam centre exists and is active
        ExamCentre daoExamCentre = examCentreDao.findById(examFileDto.getExamCentre().getId());
        if (daoExamCentre == null) {
            throw new EntityNotFoundException("ExamCentre with id: " + examFileDto.getExamCentre().getId() + " not found");
        }
        if (Boolean.FALSE.equals(daoExamCentre.getActive())) {
            throw new EntityNotFoundException("ExamCentre with id: " + daoExamCentre.getId() + " is not active. Must activate first.");
        }
        // check exam slot exists and is active
        ExamSlot daoExamSlot = examSlotDao.findById(examFileDto.getExamSlot().getId());
        if (daoExamSlot == null) {
            throw new EntityNotFoundException("ExamSlot with id: " + examFileDto.getExamSlot().getId() + " not found");
        }
        if (Boolean.FALSE.equals(daoExamSlot.getActive())) {
            throw new EntityNotFoundException("ExamSlot with id: " + daoExamSlot.getId() + " is not active. Must activate first.");
        }

        // check file type exists and is active
        FileType daoFileType = fileTypeDao.findById(examFileDto.getFileType().getId());
        if (daoFileType == null) {
            throw new EntityNotFoundException("FileType with id: " + examFileDto.getFileType().getId() + " not found");
        }
        if (Boolean.FALSE.equals(daoFileType.getActive())) {
            throw new EntityNotFoundException("FileType with id: " + daoFileType.getId() + " is not active. Must activate first.");
        }

        String regionCode = daoExamCentre.getRegion().getCode() + "";
        // TODO: when retrieving exam file, code must be replaced like how it is replaced during creation.
        // to avoid issues while creating folder.
        String replacedExamCentreCode = daoExamCentre.getCode().replaceAll("[^a-zA-Z0-9.-]", "_");

        // date =2024-06-24 16:30 PM
        LocalDate localDate = examFileDto.getExamDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String dateString = localDate.getYear() + "-" + localDate.getMonthValue() + "-" + localDate.getDayOfMonth();
        String examSlotCodeStr = daoExamSlot.getCode() + "";

        Path firstLevelDir = Path.of("/", "data");
        Path secondLevelDir = Path.of("/data", "exam-backup");
        Path thirdLevelDir = Path.of("/data/exam-backup", regionCode);
        Path fourthLevelDir = Path.of("/data/exam-backup/" + regionCode, replacedExamCentreCode);
        Path fifthLevelDir = Path.of("/data/exam-backup/" + regionCode + "/" + replacedExamCentreCode, dateString);
        Path sixthLevelDir = Path.of("/data/exam-backup/" + regionCode + "/" + replacedExamCentreCode + "/" + dateString, examSlotCodeStr);

        try {
            if (!Files.exists(firstLevelDir)) {
                Files.createDirectory(firstLevelDir);
            }
            if (!Files.exists(secondLevelDir)) {
                Files.createDirectory(secondLevelDir);
            }
            if (!Files.exists(thirdLevelDir)) {
                Files.createDirectory(thirdLevelDir);
            }
            if (!Files.exists(fourthLevelDir)) {
                Files.createDirectory(fourthLevelDir);
            }
            if (!Files.exists(fifthLevelDir)) {
                Files.createDirectory(fifthLevelDir);
            }
            if (!Files.exists(sixthLevelDir)) {
                Files.createDirectory(sixthLevelDir);
            }
        } catch (Exception ex) {
            log.error("Error creating directory.", ex);
            throw new RuntimeException("Error creating directory.");
        }

        String filePath = sixthLevelDir + "/" + examFileDto.getFileType().getName();
        // saves the file to local fs.
        try {
            log.info("**saving file to path: {}", filePath);
            examFileDto.getFile().transferTo(Path.of(filePath));
        } catch (IOException ex) {
            log.error("File transfer error.", ex);
            // RuntimeException will be handled by Controller Advice and will be sent to client as INTERNAL_SERVER_ERROR
            throw new RuntimeException("Error occurred saving file.");
        }

        ExamFile examFile = new ExamFile();
        examFile.setExamCentre(daoExamCentre);
        examFile.setExamSlot(daoExamSlot);
        examFile.setFileType(daoFileType);
        examFile.setExamDate(examFileDto.getExamDate());

        examFile.setFilePath(filePath);
        examFile.setFileSize(examFileDto.getFile().getSize());
        examFile.setContentType(examFileDto.getFile().getContentType() != null ? examFileDto.getFile().getContentType() : "not defined in the uploaded file");
        examFile.setUserUploadedFilename(examFileDto.getFile().getName());
        return examFileDao.save(examFile);
    }
}
