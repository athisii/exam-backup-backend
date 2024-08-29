package com.cdac.exambackup.controller;

import com.cdac.exambackup.dto.ExamSlotReqDto;
import com.cdac.exambackup.dto.ListRequest;
import com.cdac.exambackup.dto.ResIdDto;
import com.cdac.exambackup.dto.ResponseDto;
import com.cdac.exambackup.entity.ExamSlot;
import com.cdac.exambackup.service.BaseService;
import com.cdac.exambackup.service.ExamSlotService;
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
@Tag(name = "Exam Slot")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/exam-slots")
public class ExamSlotController extends AbstractBaseController<ExamSlot, Long> {
    static final SimpleBeanPropertyFilter commonPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "active", "examDate", "examCentre", "createdDate", "modifiedDate");

    @Autowired
    ExamSlotService examSlotService;

    public ExamSlotController(BaseService<ExamSlot, Long> baseService) {
        super(baseService);
    }

    @Override
    @GetMapping(value = {"/{id}"}, produces = {"application/json"})
    public ResponseDto<?> get(@PathVariable("id") @Valid Long id) {
        log.info("Find Request for the ExamSlot entity in the controller with id: {}", id);
        return new ResponseDto<>("Data fetched successfully.", JsonNodeUtil.getJsonNode(commonPropertyFilter, this.examSlotService.getById(id)));
    }

    @Override
    @GetMapping(produces = {"application/json"})
    public ResponseDto<?> getAll() {
        log.info("GetAll Request for the ExamSlot entity in the controller");
        return new ResponseDto<>("Data fetched successfully.", JsonNodeUtil.getJsonNode(commonPropertyFilter, this.examSlotService.getAll()));
    }

    @Override
    @PostMapping(value = {"/filtered-list"}, produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> list(@RequestBody ListRequest listRequest) {
        log.info("List Request for the ExamSlot entity in the controller");
        return new ResponseDto<>("Filtered List fetched successfully.", JsonNodeUtil.getJsonNode(commonPropertyFilter, this.examSlotService.list(listRequest)));
    }

    // this method should not be used, so overriding the parent class.
    @Hidden // hide from swagger ui
    @Override
    @PostMapping(value = {"/new"}, produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> create(@RequestBody ExamSlot entity) {
        log.info("Create Request for the entity in abstract controller.");
        return new ResponseDto<>("Your data has been saved successfully.", new ResIdDto<>(this.examSlotService.save(entity).getId()));
    }


    @PostMapping(value = {"/create"}, produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> create(@RequestBody ExamSlotReqDto examSlotReqDto) {
        log.info("Create Request for the ExamSlot entity in the controller.");
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id");
        return new ResponseDto<>("Your data has been saved successfully.", JsonNodeUtil.getJsonNode(simpleBeanPropertyFilter, this.examSlotService.save(examSlotReqDto)));
    }
}
