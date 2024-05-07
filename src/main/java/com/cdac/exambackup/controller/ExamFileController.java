package com.cdac.exambackup.controller;

import com.cdac.exambackup.dto.ResponseDto;
import com.cdac.exambackup.entity.ExamFile;
import com.cdac.exambackup.service.BaseService;
import com.cdac.exambackup.service.ExamFileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping(value = {"/create"}, produces = {"application/json"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Override
    public ResponseDto<?> create(@RequestBody @Valid ExamFile entity) {
        // TODO:: log user id?
        log.info("Create request for the entity by userId: ");
        return new ResponseDto<>("Your data has been saved successfully", this.examFileService.save(entity).getId());
    }
}
