package com.cdac.exambackup.controller;

import com.cdac.exambackup.dto.AppUserReqDto;
import com.cdac.exambackup.dto.ListRequest;
import com.cdac.exambackup.dto.ResponseDto;
import com.cdac.exambackup.entity.AppUser;
import com.cdac.exambackup.security.SecurityUtil;
import com.cdac.exambackup.service.AppUserService;
import com.cdac.exambackup.service.BaseService;
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
@Tag(name = "AppUser")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/app-users")
public class AppUserController extends AbstractBaseController<AppUser, Long> {
    static final String FETCH_SUCCESS_MSG = "Data fetched successfully.";
    static final String SAVE_SUCCESS_MSG = "Your data has been saved successfully.";
    static final SimpleBeanPropertyFilter commonPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "userId", "name", "isRegionHead", "regionId", "active", "createdDate", "modifiedDate");

    @Autowired
    AppUserService appUserService;
    @Autowired
    SecurityUtil securityUtil;

    public AppUserController(BaseService<AppUser, Long> baseService) {
        super(baseService);
    }

    @Override
    @GetMapping(value = {"/{id}"}, produces = {"application/json"})
    public ResponseDto<?> get(@PathVariable("id") @Valid Long id) {
        log.info("Find Request for the AppUser entity in the controller with id: {}", id);
        return new ResponseDto<>(FETCH_SUCCESS_MSG, JsonNodeUtil.getJsonNode(commonPropertyFilter, this.appUserService.getById(id)));
    }

    @Override
    @GetMapping(produces = {"application/json"})
    public ResponseDto<?> getAll() {
        log.info("GetAll Request for the AppUser entity in the controller");
        return new ResponseDto<>(FETCH_SUCCESS_MSG, JsonNodeUtil.getJsonNode(commonPropertyFilter, this.appUserService.getAll()));
    }

    @Override
    @PostMapping(value = {"/filtered-list"}, produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> list(@RequestBody ListRequest listRequest) {
        log.info("List Request for the AppUser entity in the controller");
        return new ResponseDto<>("Filtered List fetched successfully.", JsonNodeUtil.getJsonNode(commonPropertyFilter, this.appUserService.list(listRequest)));
    }

    @Hidden
    @Override
    @PostMapping(value = {"/hidden-create"}, produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> create(@RequestBody AppUser appUser) {
        log.info("Simple Create Request for the AppUser entity in the controller.");
        securityUtil.hasWritePermission();
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id");
        return new ResponseDto<>(SAVE_SUCCESS_MSG, JsonNodeUtil.getJsonNode(simpleBeanPropertyFilter, this.appUserService.save(appUser)));
    }

    @PostMapping(value = {"/create"}, produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> create(@RequestBody AppUserReqDto appUserReqDto) {
        log.info("Create Request for the AppUser entity in the controller.");
        securityUtil.hasWritePermission();
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id");
        return new ResponseDto<>(SAVE_SUCCESS_MSG, JsonNodeUtil.getJsonNode(simpleBeanPropertyFilter, this.appUserService.save(appUserReqDto)));
    }

    @GetMapping(value = {"/page"}, produces = {"application/json"})
    @Operation(
            summary = "Get list of entities by page",
            description = "Loads a list of entities by page from Database",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Data fetched Successfully.\", \"status\": true, \"data\": {}}"))),
                    @ApiResponse(description = "Invalid entity code", responseCode = "400", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Entity with code: 7 not found.\", \"status\": false, \"data\": null}"))),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Internal server error occurred.\", \"status\": false, \"data\": null}"))),
            }
    )
    public ResponseDto<?> getAllByPage(@PageableDefault Pageable pageable) {
        log.info("getAllByPage Request for the AppUser entity in the controller");
        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.serializeAll();
        return new ResponseDto<>(FETCH_SUCCESS_MSG, JsonNodeUtil.getJsonNode(simpleBeanPropertyFilter, this.appUserService.getAllByPage(pageable)));
    }

    @PostMapping(value = {"/create-from-csv-file"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto<?> createFromCsvFile(MultipartFile file) {
        log.info("createFromCsvFile Request for the AppUser entity in the controller.");
        securityUtil.hasWritePermission();
        this.appUserService.bulkUpload(file);
        return new ResponseDto<>(SAVE_SUCCESS_MSG, true);
    }
}
