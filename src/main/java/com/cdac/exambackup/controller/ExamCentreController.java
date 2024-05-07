package com.cdac.exambackup.controller;

import com.cdac.exambackup.dto.ResponseDto;
import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.service.BaseService;
import com.cdac.exambackup.service.ExamCentreService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Tag(name = "Exam Centre Controller")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/exam-centres")
public class ExamCentreController extends AbstractBaseController<ExamCentre, Long> {
    @Autowired
    ExamCentreService examCentreService;

    public ExamCentreController(BaseService<ExamCentre, Long> baseService) {
        super(baseService);
    }

    @PostMapping(value = {"/create"}, produces = {"application/json"}, consumes = {"application/json"})
    @Override
    public ResponseDto<?> create(@RequestBody @Valid ExamCentre examCentre) {
        log.info("Create Request for the entity in abstract controller.");
        return new ResponseDto<>("Your data has been saved successfully", this.examCentreService.save(examCentre).getId());
    }
}
