package com.cdac.exambackup.controller;

import com.cdac.exambackup.dto.ListRequest;
import com.cdac.exambackup.dto.ResIdDto;
import com.cdac.exambackup.dto.ResponseDto;
import com.cdac.exambackup.entity.AuditModel;
import com.cdac.exambackup.service.BaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/5/24
 */

@Slf4j
public abstract class AbstractBaseController<E extends AuditModel, K extends Serializable> {
    private final BaseService<E, K> baseService;

    protected AbstractBaseController(BaseService<E, K> baseService) {
        this.baseService = baseService;
    }

    /*
     * to be overridden by the subclass for validation,
     * subclass should apply the logic to save data
     * ignore some fields even if passed by client like, active, etc
     */
    @PostMapping(value = {"/create"}, produces = {"application/json"}, consumes = {"application/json"})
    @Operation(
            summary = "Create/Update entity",
            description = "Create or Update (if Id passed) the entity in Database",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Your data has been saved successfully\", \"status\": true, \"data\": {\"id\":1}}"))),
                    @ApiResponse(description = "Validation failure / invalid request payload", responseCode = "400", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Invalid request/validation error message.\", \"status\": false, \"data\": null}"))),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Internal server error occurred.\", \"status\": false, \"data\": null}"))),
            }
    )
    public ResponseDto<?> create(@RequestBody E entity) {
        log.info("Create Request for the entity in abstract controller.");
        return new ResponseDto<>("Your data has been saved successfully", new ResIdDto<>(this.baseService.save(entity).getId()));
    }

    @GetMapping(value = {"/{id}"}, produces = {"application/json"})
    @Operation(
            summary = "Get Entity",
            description = "Loads a single entity from Database corresponds to passed Id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Data fetched Successfully.\", \"status\": true, \"data\": {}}"))),
                    @ApiResponse(description = "Invalid entity id", responseCode = "400", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Entity with id: 7 not found.\", \"status\": false, \"data\": null}"))),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Internal server error occurred.\", \"status\": false, \"data\": null}"))),
            }
    )
    public ResponseDto<?> get(@Schema(name = "id", description = "Entity Id of type Long", type = "Long", defaultValue = "1") @Parameter(in = ParameterIn.PATH) @PathVariable("id") K id) {
        E e = this.baseService.getById(id);
        log.info("Find Request for the entity in abstract controller -> {}", id);
        return new ResponseDto<>("Data fetched Successfully", e);
    }

    @GetMapping(produces = {"application/json"})
    @Operation(
            summary = "List of Entity",
            description = "Loads all the active available entities in Database",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Data fetched Successfully\", \"status\": true, \"data\": [{}]}"))),
                    @ApiResponse(description = "Bad request", responseCode = "400", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Bad request.\", \"status\": false, \"data\": null}"))),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Internal server error occurred.\", \"status\": false, \"data\": null}"))),
            }
    )
    public ResponseDto<?> getAll() {
        List<E> data = this.baseService.getAll();
        log.info("GetAll Request for the entity in abstract controller");
        return new ResponseDto<>("Data fetched successfully", data);
    }

    @PostMapping({"/deactivate/{id}"})
    @Operation(
            summary = "Deactivate Entity",
            description = "Deactivates the passed Entity Id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Entity has been deactivated successfully\", \"status\": true, \"data\": {\"id\": 1}}"))),
                    @ApiResponse(description = "Invalid entity id", responseCode = "400", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Entity with id: 7 not found.\", \"status\": false, \"data\": null}"))),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Internal server error occurred.\", \"status\": false, \"data\": null}"))),
            }
    )
    public ResponseDto<?> deactivate(@Schema(name = "id", description = "Entity Id of type Long", type = "Long", defaultValue = "1") @Parameter(in = ParameterIn.PATH) @PathVariable @Valid K id) {
        this.baseService.deactivateById(id);
        log.info("Deactivate Request for the entity in abstract controller ->{}", id);
        return new ResponseDto<>("Entity has been deactivated successfully", new ResIdDto<>(id));
    }

    @PostMapping({"/activate/{id}"})
    @Operation(
            summary = "Activate Entity",
            description = "Activates the passed Entity Id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Entity has been activated successfully\", \"status\": true, \"data\": {\"id\": 1}}"))),
                    @ApiResponse(description = "Invalid entity id", responseCode = "400", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Entity with id: 7 not found.\", \"status\": false, \"data\": null}"))),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Internal server error occurred.\", \"status\": false, \"data\": null}"))),
            })
    public ResponseDto<?> activate(@Schema(name = "id", description = "Entity Id of type Long", type = "Long", defaultValue = "1") @Parameter(in = ParameterIn.PATH) @PathVariable @Valid K id) {
        this.baseService.activateById(id);
        log.info("Activate Request for the entity in abstract controller ->{}", id);
        return new ResponseDto<>("Entity has been activated successfully", new ResIdDto<>(id));
    }

    @PostMapping({"/soft-delete/{id}"})
    @Operation(
            summary = "Soft delete Entity",
            description = "Soft deletes the passed Entity Id",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Entity has been deleted successfully\", \"status\": true, \"data\": {\"id\": 1}}"))),
                    @ApiResponse(description = "Invalid entity id", responseCode = "400", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Entity with id: 7 not found.\", \"status\": false, \"data\": null}"))),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Internal server error occurred.\", \"status\": false, \"data\": null}"))),
            }
    )
    public ResponseDto<?> softDelete(@Schema(name = "id", description = "Entity Id of type Long", type = "Long", defaultValue = "1") @Parameter(in = ParameterIn.PATH) @PathVariable @Valid K id) {
        this.baseService.softDeleteById(id);
        log.info("Delete Request for the entity in abstract controller ->{}", id);
        return new ResponseDto<>("Entity has been deleted successfully", new ResIdDto<>(id));
    }

    @PostMapping(value = {"/filtered-list"}, produces = {"application/json"}, consumes = {"application/json"})
    @Operation(
            summary = "List of Entity",
            description = "Filtered List of entities on the basis of passed filters",
            responses = {
                    @ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Filtered List fetched successfully\", \"status\": true, \"data\": [{}]}"))),
                    @ApiResponse(description = "Bad request", responseCode = "400", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Bad request.\", \"status\": false, \"data\": null}"))),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content(schema = @Schema(name = "ResponseDto", example = "{\"message\":\"Internal server error occurred.\", \"status\": false, \"data\": null}"))),
            }
    )
    public ResponseDto<?> list(@RequestBody ListRequest listRequest) {
        List<E> list = this.baseService.list(listRequest);
        log.info("List Request for the entity in abstract controller");
        return new ResponseDto<>("Filtered List fetched successfully", list);
    }
}
