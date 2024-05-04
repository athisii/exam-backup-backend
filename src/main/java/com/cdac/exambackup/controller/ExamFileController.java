package com.cdac.exambackup.controller;

import com.cdac.exambackup.entity.ExamFile;
import com.cdac.exambackup.service.BaseService;
import com.cdac.exambackup.service.ExamFileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Tag(name = "Exam File Controller")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/exam-files")
public class ExamFileController extends AbstractBaseController<ExamFile, Long> {
    @Autowired
    ExamFileService examFileService;

    public ExamFileController(BaseService<ExamFile, Long> baseService) {
        super(baseService);
    }
}
