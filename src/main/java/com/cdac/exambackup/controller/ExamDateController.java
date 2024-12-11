package com.cdac.exambackup.controller;

import com.cdac.exambackup.dto.ExamDateReqDto;
import com.cdac.exambackup.dto.ListRequest;
import com.cdac.exambackup.dto.ResIdDto;
import com.cdac.exambackup.dto.ResponseDto;
import com.cdac.exambackup.entity.ExamDate;
import com.cdac.exambackup.security.SecurityUtil;
import com.cdac.exambackup.service.BaseService;
import com.cdac.exambackup.service.ExamDateService;
import com.cdac.exambackup.util.JsonNodeUtil;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    private static final String FETCH_SUCCESS_MSG = "Data fetched successfully.";
    static final SimpleBeanPropertyFilter commonPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "active", "date", "createdDate", "modifiedDate");

    @Autowired
    ExamDateService examDateService;
    @Autowired
    SecurityUtil securityUtil;

    public ExamDateController(BaseService<ExamDate, Long> baseService) {
        super(baseService);
    }

    @Override
    @GetMapping(value = {"/{id}"}, produces = {"application/json"})
    public ResponseDto<?> get(@PathVariable("id") @Valid Long id) {
        log.info("Find Request for the ExamDate entity in the controller with id: {}", id);
        return new ResponseDto<>(FETCH_SUCCESS_MSG, JsonNodeUtil.getJsonNode(commonPropertyFilter, this.examDateService.getById(id)));
    }

    @Override
    @GetMapping(produces = {"application/json"})
    public ResponseDto<?> getAll() {
        log.info("GetAll Request for the ExamDate entity in the controller");
        return new ResponseDto<>(FETCH_SUCCESS_MSG, JsonNodeUtil.getJsonNode(commonPropertyFilter, this.examDateService.getAll()));
    }

    @Override
    @PostMapping(value = {"/filtered-list"}, produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> list(@RequestBody ListRequest listRequest) {
        log.info("List Request for the ExamDate entity in the controller");
        return new ResponseDto<>("Filtered List fetched successfully.", JsonNodeUtil.getJsonNode(commonPropertyFilter, this.examDateService.list(listRequest)));
    }

    // this method should not be used, so overriding the parent class.
    @Hidden // hide from swagger ui
    @Override
    @PostMapping(value = {"/new"}, produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> create(@RequestBody ExamDate entity) {
        log.info("Create Request for the entity in abstract controller.");
        securityUtil.hasWritePermission();
        return new ResponseDto<>("Your data has been saved successfully.", new ResIdDto<>(this.examDateService.save(entity).getId()));
    }


    @PostMapping(value = {"/create"}, produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> create(@RequestBody ExamDateReqDto examDateReqDto) {
        log.info("Create Request for the ExamDate entity in the controller.");
        securityUtil.hasWritePermission();
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id");
        return new ResponseDto<>("Your data has been saved successfully.", JsonNodeUtil.getJsonNode(simpleBeanPropertyFilter, this.examDateService.save(examDateReqDto)));
    }

    @GetMapping(value = {"/query"}, produces = {"application/json"})
    @Operation(
            summary = "Get list of entities by page",
            description = "Loads a list of entities by page from Database corresponds to requested exam centre id.",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Data fetched Successfully.\", \"status\": true, \"data\": {}}"))),
                    @ApiResponse(description = "Invalid entity code", responseCode = "400", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Entity with code: 7 not found.\", \"status\": false, \"data\": null}"))),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Internal server error occurred.\", \"status\": false, \"data\": null}"))),
            }
    )
    public ResponseDto<?> getByExamCentreId(@RequestParam Long examCentreId, @PageableDefault Pageable pageable) {
        log.info("Query Request for the ExamDate entity in the controller for exam centre id: " + examCentreId);
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.serializeAll();
        return new ResponseDto<>(FETCH_SUCCESS_MSG, JsonNodeUtil.getJsonNode(simpleBeanPropertyFilter, this.examDateService.getByExamCentreId(examCentreId, pageable)));
    }

    @GetMapping(value = {"/page"}, produces = {"application/json"})
    @Operation(
            summary = "Get list of entities by page",
            description = "Loads a list of entities by page from Database corresponds",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Data fetched Successfully.\", \"status\": true, \"data\": {}}"))),
                    @ApiResponse(description = "Invalid entity code", responseCode = "400", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Entity with code: 7 not found.\", \"status\": false, \"data\": null}"))),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Internal server error occurred.\", \"status\": false, \"data\": null}"))),
            }
    )
    public ResponseDto<?> getAllByPage(@PageableDefault Pageable pageable) {
        log.info("getAllByPage Request for the Exam Date entity in the controller");
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.serializeAll();
        return new ResponseDto<>(FETCH_SUCCESS_MSG, JsonNodeUtil.getJsonNode(simpleBeanPropertyFilter, this.examDateService.getAllByPage(pageable)));
    }
}
