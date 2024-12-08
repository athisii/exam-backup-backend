package com.cdac.exambackup.controller;

import com.cdac.exambackup.dto.ExamFileReqDto;
import com.cdac.exambackup.dto.ListRequest;
import com.cdac.exambackup.dto.ResIdDto;
import com.cdac.exambackup.dto.ResponseDto;
import com.cdac.exambackup.entity.ExamFile;
import com.cdac.exambackup.service.BaseService;
import com.cdac.exambackup.service.ExamFileService;
import com.cdac.exambackup.util.JsonNodeUtil;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Exam File")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/exam-files")
public class ExamFileController extends AbstractBaseController<ExamFile, Long> {
    private static final String FETCH_SUCCESS_MSG = "Data fetched successfully.";
    static final SimpleBeanPropertyFilter commonPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "examCentre", "slot", "fileType", "examDate", "filePath", "fileSize", "contentType", "userUploadedFilename");

    @Autowired
    ExamFileService examFileService;

    public ExamFileController(BaseService<ExamFile, Long> baseService) {
        super(baseService);
    }

    @Override
    @GetMapping(value = {"/{id}"}, produces = {"application/json"})
    public ResponseDto<?> get(@PathVariable("id") @Valid Long id) {
        log.info("Find Request for the ExamFile entity in the controller with id: {}", id);
        return new ResponseDto<>(FETCH_SUCCESS_MSG, JsonNodeUtil.getJsonNode(commonPropertyFilter, this.examFileService.getById(id)));
    }

    @Override
    @GetMapping(produces = {"application/json"})
    public ResponseDto<?> getAll() {
        log.info("GetAll Request for the ExamFile entity in the controller");
        return new ResponseDto<>(FETCH_SUCCESS_MSG, JsonNodeUtil.getJsonNode(commonPropertyFilter, this.examFileService.getAll()));
    }

    @Override
    @PostMapping(value = {"/filtered-list"}, produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> list(@RequestBody ListRequest listRequest) {
        log.info("List Request for the ExamFile entity in the controller");
        return new ResponseDto<>("Filtered List fetched successfully.", JsonNodeUtil.getJsonNode(commonPropertyFilter, this.examFileService.list(listRequest)));
    }

    // this method should not be used, so overriding the parent class.
    @Hidden // hide from swagger ui
    @Override
    @PostMapping(value = {"/hidden-create"}, produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> create(@RequestBody ExamFile entity) {
        log.info("Create Request for the entity in abstract controller.");
        return new ResponseDto<>("Your data has been saved successfully.", new ResIdDto<>(this.examFileService.save(entity).getId()));
    }


    @PostMapping(value = {"/create"}, produces = {"application/json"}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Create/Update entity with MultipartFile", description = "Create or Update (if Id passed) the entity in Database", responses = {@ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Your data has been saved successfully.\", \"status\": true, \"data\": {\"id\":1}}"))), @ApiResponse(description = "Validation failure / invalid request payload", responseCode = "400", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Invalid request/validation error message.\", \"status\": false, \"data\": null}"))), @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Internal server error occurred.\", \"status\": false, \"data\": null}"))),})
    public ResponseDto<?> create(ExamFileReqDto examFileReqDto) {
        log.info("Create Request for the ExamFile entity in the controller.");
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id");
        return new ResponseDto<>("Your data has been saved successfully.", JsonNodeUtil.getJsonNode(simpleBeanPropertyFilter, this.examFileService.save(examFileReqDto)));
    }

    @GetMapping(value = {"/query"}, produces = {"application/json"})
    @Operation(summary = "Returns list of ExamFiles matching centre Id, exam date and slot", description = "Loads all the active available entities based on requested centre code, exam date and slot", responses = {@ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Data fetched successfully.\", \"status\": true, \"data\": [{}]}"))), @ApiResponse(description = "Bad request", responseCode = "400", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Bad request.\", \"status\": false, \"data\": null}"))), @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Internal server error occurred.\", \"status\": false, \"data\": null}"))),})
    public ResponseDto<?> getByCentreCentreExamDateAndSlot(@RequestParam Long examCentreId, @RequestParam Long examDateId, @RequestParam Long slotId) {
        log.info("Query request for the ExamFile entity in the controller.");
        return new ResponseDto<>(FETCH_SUCCESS_MSG, JsonNodeUtil.getJsonNode(commonPropertyFilter, this.examFileService.findByCentreCentreIdExamDateIdAndSlotId(examCentreId, examDateId, slotId)));
    }

    @GetMapping(value = "/download/{id}", produces = "application/octet-stream")
    public ResponseEntity<Resource> downloadFile(@PathVariable("id") @Valid Long id) {
        log.info("Download Request for the file with id: {}", id);
        try {
            ExamFile dbExamFile = this.examFileService.getById(id);
            Path filePath = Paths.get(dbExamFile.getFilePath()).toAbsolutePath();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + dbExamFile.getUserUploadedFilename());
            headers.add(HttpHeaders.CONTENT_TYPE, dbExamFile.getContentType());
            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
