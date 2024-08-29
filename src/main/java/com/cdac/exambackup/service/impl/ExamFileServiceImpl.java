package com.cdac.exambackup.service.impl;

import com.cdac.exambackup.dao.*;
import com.cdac.exambackup.dto.ExamFileReqDto;
import com.cdac.exambackup.entity.*;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import com.cdac.exambackup.service.ExamFileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

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
    SlotDao slotDao;

    @Autowired
    ExamDateDao examDateDao;

    @Autowired
    FileTypeDao fileTypeDao;

    public ExamFileServiceImpl(BaseDao<ExamFile, Long> baseDao) {
        super(baseDao);
    }

    @Transactional(readOnly = true)
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
        // check exam centre exists and is active; findById() only returns active entity else null

        if (examFileDto.getExamCentre() == null || examFileDto.getSlot() == null || examFileDto.getFileType() == null || examFileDto.getExamDate() == null || examFileDto.getFile() == null) {
            throw new InvalidReqPayloadException("Please provide all the required data.");
        }

        if (examFileDto.getExamCentre().getId() == null || examFileDto.getSlot().getId() == null || examFileDto.getFileType().getId() == null || examFileDto.getExamDate().getId() == null) {
            throw new InvalidReqPayloadException("Please provide all the required ids.");
        }
        if (examFileDto.getFile().isEmpty()) {
            throw new InvalidReqPayloadException("Selected file is an empty file.");
        }

        // check entities exists and is active; findById() only returns active entity else null

        ExamCentre daoExamCentre = examCentreDao.findById(examFileDto.getExamCentre().getId());
        if (daoExamCentre == null) {
            throw new EntityNotFoundException("ExamCentre with id: " + examFileDto.getExamCentre().getCode() + " not found");
        }
        Slot daoSlot = slotDao.findById(examFileDto.getSlot().getId());
        if (daoSlot == null) {
            throw new EntityNotFoundException("Slot with id: " + examFileDto.getSlot().getId() + " not found");
        }

        FileType daoFileType = fileTypeDao.findById(examFileDto.getFileType().getId());
        if (daoFileType == null) {
            throw new EntityNotFoundException("FileType with id: " + examFileDto.getFileType().getId() + " not found");
        }

        ExamDate daoExamDate = examDateDao.findById(examFileDto.getExamDate().getId());
        if (daoExamDate == null) {
            throw new EntityNotFoundException("ExamDate with id: " + examFileDto.getExamDate().getId() + " not found");
        }

        /* prepares for folder structure and filename
           creates folder structure like: /data/exam-backup/regionCode/examCentreId/examDateId/examSlot
           check and create if not existed
        */

        // TODO: when retrieving exam file, code must be replaced like how it is replaced during creation.
        // to avoid issues while creating folder.
        String regionCode = daoExamCentre.getRegion().getCode().replaceAll("[^a-zA-Z0-9.-]", "_");
        String examCentreCode = daoExamCentre.getCode().replaceAll("[^a-zA-Z0-9.-]", "_");
        String slotCode = daoSlot.getCode().replaceAll("[^a-zA-Z0-9.-]", "_");

        LocalDate examDate = examFileDto.getExamDate().getDate();

        // date =2024-06-24 16:30 PM
        String dateStr = examDate.getYear() + "-" + examDate.getMonthValue() + "-" + examDate.getDayOfMonth();

        Path firstLevelDir = Path.of("/", "data");
        Path secondLevelDir = Path.of("/data", "exam-backup");
        Path thirdLevelDir = Path.of("/data/exam-backup", regionCode);
        Path fourthLevelDir = Path.of("/data/exam-backup/" + regionCode, examCentreCode);
        Path fifthLevelDir = Path.of("/data/exam-backup/" + regionCode + "/" + examCentreCode, dateStr);
        Path sixthLevelDir = Path.of("/data/exam-backup/" + regionCode + "/" + examCentreCode + "/" + dateStr, slotCode);

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
            // RuntimeException will be handled by Controller Advice and will be sent to client as INTERNAL_SERVER_ERROR
            throw new RuntimeException("Error occurred while creating directory.");
        }

        // check if there is an entry for the same file type.
        ExamFile daoExamFile = examFileDao.findByExamCentreAndExamDateAndSlotAndFileType(daoExamCentre, daoExamDate, daoSlot, daoFileType);

        // first entry, no duplicate found.
        if (daoExamFile == null) {
            String filePath = sixthLevelDir + "/" + examFileDto.getFile().getOriginalFilename();
            // saves the file to local fs.
            try {
                log.info("**saving file to path: {}", filePath);
                examFileDto.getFile().transferTo(Path.of(filePath));
            } catch (Exception ex) {
                log.error("File transfer error.", ex);
                // RuntimeException will be handled by Controller Advice and will be sent to client as INTERNAL_SERVER_ERROR
                throw new RuntimeException("Error occurred while saving file.");
            }
            ExamFile examFile = new ExamFile();
            examFile.setExamCentre(daoExamCentre);
            examFile.setSlot(daoSlot);
            examFile.setFileType(daoFileType);
            examFile.setExamDate(daoExamDate);

            examFile.setFilePath(filePath);
            examFile.setFileSize(examFileDto.getFile().getSize());
            examFile.setContentType(examFileDto.getFile().getContentType() != null ? examFileDto.getFile().getContentType() : "unknown content type");
            examFile.setUserUploadedFilename(examFileDto.getFile().getOriginalFilename());
            return examFileDao.save(examFile);
        }
        // daoExamFile is not null. Same file already exists.
        // already uploaded, re-uploading the same file type again.

        String oldFilePath = sixthLevelDir + "/" + daoExamFile.getUserUploadedFilename();
        String newFilePath = sixthLevelDir + "/" + examFileDto.getFile().getOriginalFilename();
        try {
            log.info("**deleting previously stored file: {}", oldFilePath);
            Files.deleteIfExists(Path.of(oldFilePath));
            examFileDto.getFile().transferTo(Path.of(newFilePath));
        } catch (Exception ex) {
            log.error("Error occurred while deleting/saving file.", ex);
            // RuntimeException will be handled by Controller Advice and will be sent to client as INTERNAL_SERVER_ERROR
            throw new RuntimeException("Error occurred saving/deleting file.");
        }

        daoExamFile.setFilePath(newFilePath);
        daoExamFile.setFileSize(examFileDto.getFile().getSize());
        daoExamFile.setContentType(examFileDto.getFile().getContentType() != null ? examFileDto.getFile().getContentType() : "unknown content type");
        daoExamFile.setUserUploadedFilename(examFileDto.getFile().getOriginalFilename());
        return examFileDao.save(daoExamFile);
    }

    @Transactional
    @Override
    public ExamFile save(ExamFileReqDto examFileReqDto) {
        var examFile = new ExamFile();
        examFile.setId(examFileReqDto.id());

        // examCentre
        var examCentre = new ExamCentre();
        examCentre.setId(examFileReqDto.examCentreId());
        examFile.setExamCentre(examCentre);

        // slot
        var slot = new Slot();
        slot.setId(examFileReqDto.slotId());
        examFile.setSlot(slot);

        // fileType
        var fileType = new FileType();
        fileType.setId(examFileReqDto.fileTypeId());
        examFile.setFileType(fileType);

        // ExamDate
        var examDate = new ExamDate();
        examDate.setId(examFileReqDto.examDateId());
        examFile.setExamDate(examDate);

        examFile.setFile(examFileReqDto.file());
        return save(examFile);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ExamFile> findByCentreCentreExamDateAndSlot(ExamFileReqDto examFileReqDto) {
        if (examFileReqDto.examCentreId() == null || examFileReqDto.examDateId() == null || examFileReqDto.slotId() == null) {
            throw new InvalidReqPayloadException("Please provide all the required ids.");
        }

        // check exam centre exists and is active; findById() only returns active entity else null
        ExamCentre daoExamCentre = examCentreDao.findById(examFileReqDto.examCentreId());
        if (daoExamCentre == null) {
            throw new EntityNotFoundException("ExamCentre with id: " + examFileReqDto.examCentreId() + " not found");
        }
        Slot daoSlot = slotDao.findById(examFileReqDto.slotId());
        if (daoSlot == null) {
            throw new EntityNotFoundException("Slot with id: " + examFileReqDto.slotId() + " not found");
        }

        ExamDate daoExamDate = examDateDao.findById(examFileReqDto.examDateId());
        if (daoExamDate == null) {
            throw new EntityNotFoundException("ExamDate with id: " + examFileReqDto.examDateId() + " not found");
        }
        return examFileDao.findByExamCentreAndExamDateAndSlot(daoExamCentre, daoExamDate, daoSlot);
    }
}
