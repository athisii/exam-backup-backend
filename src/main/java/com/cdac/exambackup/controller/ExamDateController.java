package com.cdac.exambackup.controller;

import com.cdac.exambackup.dto.ExamDateReqDto;
import com.cdac.exambackup.dto.ListRequest;
import com.cdac.exambackup.dto.ResIdDto;
import com.cdac.exambackup.dto.ResponseDto;
import com.cdac.exambackup.entity.ExamDate;
import com.cdac.exambackup.service.BaseService;
import com.cdac.exambackup.service.ExamDateService;
import com.cdac.exambackup.util.JsonNodeUtil;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Exam Date")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/exam-dates")
public class ExamDateController extends AbstractBaseController<ExamDate, Long> {
    static final SimpleBeanPropertyFilter commonPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "active", "examDate", "examCentre", "examSlots", "createdDate", "modifiedDate");

    @Autowired
    ExamDateService examDateService;

    public ExamDateController(BaseService<ExamDate, Long> baseService) {
        super(baseService);
    }

    @Override
    @GetMapping(value = {"/{id}"}, produces = {"application/json"})
    public ResponseDto<?> get(@PathVariable("id") @Valid Long id) {
        log.info("Find Request for the ExamDate entity in the controller with id: {}", id);
        return new ResponseDto<>("Data fetched Successfully", JsonNodeUtil.getJsonNode(commonPropertyFilter, this.examDateService.getById(id)));
    }

    @Override
    @GetMapping(produces = {"application/json"})
    public ResponseDto<?> getAll() {
        log.info("GetAll Request for the ExamSExamDatelot entity in the controller");
        return new ResponseDto<>("Data fetched successfully", JsonNodeUtil.getJsonNode(commonPropertyFilter, this.examDateService.getAll()));
    }

    @Override
    @PostMapping(value = {"/filtered-list"}, produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> list(@RequestBody ListRequest listRequest) {
        log.info("List Request for the ExamDate entity in the controller");
        return new ResponseDto<>("Filtered List fetched successfully", JsonNodeUtil.getJsonNode(commonPropertyFilter, this.examDateService.list(listRequest)));
    }

    // this method should not be used, so overriding the parent class.
    @Hidden // hide from swagger ui
    @Override
    @PostMapping(value = {"/new"}, produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> create(@RequestBody ExamDate entity) {
        log.info("Create Request for the entity in abstract controller.");
        return new ResponseDto<>("Your data has been saved successfully", new ResIdDto<>(this.examDateService.save(entity).getId()));
    }


    @PostMapping(value = {"/create"}, produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> create(@RequestBody ExamDateReqDto examDateReqDto) {
        log.info("Create Request for the ExamDate entity in the controller.");
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id");
        return new ResponseDto<>("Your data has been saved successfully", JsonNodeUtil.getJsonNode(simpleBeanPropertyFilter, this.examDateService.save(examDateReqDto)));
    }
}
