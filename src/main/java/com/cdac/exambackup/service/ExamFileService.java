package com.cdac.exambackup.service;

import com.cdac.exambackup.dto.ExamFileReqDto;
import com.cdac.exambackup.entity.ExamFile;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface ExamFileService extends BaseService<ExamFile, Long> {
    ExamFile save(ExamFileReqDto examFileReqDto);

    List<ExamFile> findByCentreCodeExamDateAndSlot(ExamFileReqDto examFileReqDto);
}
