package com.cdac.exambackup.controller;

import com.cdac.exambackup.dto.ExamCentreReqDto;
import com.cdac.exambackup.dto.ExamCentreSlotUpdateReqDto;
import com.cdac.exambackup.dto.ListRequest;
import com.cdac.exambackup.dto.ResponseDto;
import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.service.BaseService;
import com.cdac.exambackup.service.ExamCentreService;
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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Exam Centre")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/exam-centres")
public class ExamCentreController extends AbstractBaseController<ExamCentre, Long> {
    static final String FETCH_SUCCESS_MSG = "Data fetched successfully.";
    static final String SAVE_SUCCESS_MSG = "Your data has been saved successfully.";

    static final SimpleBeanPropertyFilter commonPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "code", "name", "region", "active", "createdDate", "modifiedDate");

    @Autowired
    ExamCentreService examCentreService;


    public ExamCentreController(BaseService<ExamCentre, Long> baseService) {
        super(baseService);
    }

    @Override
    @GetMapping(value = {"/{id}"}, produces = {"application/json"})
    public ResponseDto<?> get(@PathVariable("id") @Valid Long id) {
        log.info("Find Request for the ExamCentre entity in the controller with id: {}", id);
        return new ResponseDto<>(FETCH_SUCCESS_MSG, JsonNodeUtil.getJsonNode(commonPropertyFilter, this.examCentreService.getById(id)));
    }

    @Override
    @GetMapping(produces = {"application/json"})
    public ResponseDto<?> getAll() {
        log.info("GetAll Request for the ExamCentre entity in the controller");
        return new ResponseDto<>(FETCH_SUCCESS_MSG, JsonNodeUtil.getJsonNode(commonPropertyFilter, this.examCentreService.getAll()));
    }

    @Override
    @PostMapping(value = {"/filtered-list"}, produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> list(@RequestBody @Valid ListRequest listRequest) {
        log.info("List Request for the ExamCentre entity in the controller");
        return new ResponseDto<>("Filtered List fetched successfully.", JsonNodeUtil.getJsonNode(commonPropertyFilter, this.examCentreService.list(listRequest)));
    }

    @Hidden
    @Override
    @PostMapping(value = {"/hidden-create"}, produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> create(@RequestBody ExamCentre examCentre) {
        log.info("Simple Create Request for the ExamCentre entity in the controller.");
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id");
        return new ResponseDto<>(SAVE_SUCCESS_MSG, JsonNodeUtil.getJsonNode(simpleBeanPropertyFilter, this.examCentreService.save(examCentre)));
    }

    @PostMapping(value = {"/create"}, produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> create(@RequestBody ExamCentreReqDto examCentreReqDto) {
        log.info("Create Request for the ExamCentre entity in the controller.");
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id");
        return new ResponseDto<>(SAVE_SUCCESS_MSG, JsonNodeUtil.getJsonNode(simpleBeanPropertyFilter, this.examCentreService.save(examCentreReqDto)));
    }


    @GetMapping(value = {"/query"}, produces = {"application/json"})
    @Operation(
            summary = "Get list of entities by page",
            description = "Loads a list of entities by page from Database corresponds to requested code, name, exam date, and/or region",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Data fetched Successfully.\", \"status\": true, \"data\": {}}"))),
                    @ApiResponse(description = "Invalid entity code", responseCode = "400", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Entity with code: 7 not found.\", \"status\": false, \"data\": null}"))),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Internal server error occurred.\", \"status\": false, \"data\": null}"))),
            }
    )
    public ResponseDto<?> getByCodeOrNameOrRegionId(@RequestParam(required = false) String code, @RequestParam(required = false) String name, @RequestParam(required = false) Long regionId, @PageableDefault Pageable pageable) {
        log.info("Query Request for the ExamCentre entity in the controller for code, name, exam date, and/or region");
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.serializeAll();
        return new ResponseDto<>(FETCH_SUCCESS_MSG, JsonNodeUtil.getJsonNode(simpleBeanPropertyFilter, this.examCentreService.getByCodeOrNameOrRegionId(code, name, regionId, pageable)));
    }

    @GetMapping(value = {"/search"}, produces = {"application/json"})
    @Operation(
            summary = "Get list of entities by page",
            description = "Loads a list of entities by page from Database corresponds to search parameters",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Data fetched Successfully.\", \"status\": true, \"data\": {}}"))),
                    @ApiResponse(description = "Invalid entity code", responseCode = "400", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Entity with code: 7 not found.\", \"status\": false, \"data\": null}"))),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Internal server error occurred.\", \"status\": false, \"data\": null}"))),
            }
    )
    public ResponseDto<?> search(@RequestParam(required = false) String searchTerm, @RequestParam(required = false) Long regionId, @PageableDefault Pageable pageable) {
        log.info("Search Request for the ExamCentre entity in the controller");
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.serializeAll();
        return new ResponseDto<>(FETCH_SUCCESS_MSG, JsonNodeUtil.getJsonNode(simpleBeanPropertyFilter, this.examCentreService.search(searchTerm, regionId, pageable)));
    }

    @GetMapping(value = {"/upload-status-filter-page"}, produces = {"application/json"})
    @Operation(
            summary = "Get filtered list of entities by page based on upload status",
            description = "Loads a filtered list of entities by page from Database corresponds to search parameters",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Data fetched Successfully.\", \"status\": true, \"data\": {}}"))),
                    @ApiResponse(description = "Invalid entity code", responseCode = "400", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Entity with code: 7 not found.\", \"status\": false, \"data\": null}"))),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Internal server error occurred.\", \"status\": false, \"data\": null}"))),
            }
    )
    public ResponseDto<?> getExamCentresOnUploadStatusByPage(@RequestParam(required = false) String searchTerm, @RequestParam(required = false) String filterType, @RequestParam(required = false) Long regionId, @PageableDefault Pageable pageable) {
        log.info("Filter By Page Request for the ExamCentre entity in the controller");
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.serializeAll();
        return new ResponseDto<>(FETCH_SUCCESS_MSG, JsonNodeUtil.getJsonNode(simpleBeanPropertyFilter, this.examCentreService.getExamCentresOnUploadStatusByPage(searchTerm, filterType, regionId, pageable)));
    }

    @GetMapping(value = {"/page"}, produces = {"application/json"})
    @Operation(
            summary = "Get list of entities by page",
            description = "Loads a list of entities by page from Database corresponds to requested code, name",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Data fetched Successfully.\", \"status\": true, \"data\": {}}"))),
                    @ApiResponse(description = "Invalid entity code", responseCode = "400", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Entity with code: 7 not found.\", \"status\": false, \"data\": null}"))),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Internal server error occurred.\", \"status\": false, \"data\": null}"))),
            }
    )
    public ResponseDto<?> getAllByPage(@PageableDefault Pageable pageable) {
        log.info("getAllByPage Request for the Exam Centre entity in the controller for code, name");
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.serializeAll();
        return new ResponseDto<>(FETCH_SUCCESS_MSG, JsonNodeUtil.getJsonNode(simpleBeanPropertyFilter, this.examCentreService.getAllByPage(pageable)));
    }

    @PostMapping(value = {"/create-from-csv-file"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto<?> createFromCsvFile(MultipartFile file) {
        log.info("createFromCsvFile Request for the ExamCentre entity in the controller.");
        this.examCentreService.bulkUpload(file);
        return new ResponseDto<>(SAVE_SUCCESS_MSG, true);
    }

    @PostMapping(value = {"/update-only-slot"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto<?> updateOnlySlot(@RequestBody ExamCentreSlotUpdateReqDto examCentreSlotUpdateReqDto) {
        log.info("updateOnlySlot Request for the ExamCentre entity in the controller.");
        this.examCentreService.updateOnlySlot(examCentreSlotUpdateReqDto);
        return new ResponseDto<>(SAVE_SUCCESS_MSG, true);
    }
}
