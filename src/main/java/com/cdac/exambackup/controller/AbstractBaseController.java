package com.cdac.exambackup.controller;

import com.cdac.exambackup.dto.ListRequest;
import com.cdac.exambackup.dto.ResponseDto;
import com.cdac.exambackup.entity.AuditModel;
import com.cdac.exambackup.service.BaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Operation(summary = "Create/Update entity", description = "Create or Update (if Id passed) the entity in Database")
    public ResponseDto<?> create(@RequestBody @Valid E entity) {
        log.info("Create Request for the entity in abstract controller.");
        return new ResponseDto<>("Your data has been saved successfully", this.baseService.save(entity).getId());
    }

    @GetMapping(value = {"/{id}"}, produces = {"application/json"})
    @Operation(summary = "Get Entity", description = "Loads a single entity from Database corresponds to passed Id")
    public ResponseDto<?> get(@Schema(name = "id", description = "Entity Id of type Long", type = "Long", defaultValue = "1") @PathVariable("id") @Valid K id) {
        E e = this.baseService.getById(id);
        log.info("Find Request for the entity in abstract controller -> {}", id);
        return new ResponseDto<>("Data fetched Successfully", e);
    }

    @GetMapping(produces = {"application/json"})
    @Operation(summary = "List of Entity", description = "Loads all the active available entities in Database")
    public ResponseDto<?> getAll() {
        List<E> data = this.baseService.getAll();
        log.info("GetAll Request for the entity in abstract controller");
        return new ResponseDto<>("Data fetched successfully", data);
    }

    @PostMapping({"/deactivate/{id}"})
    @Operation(summary = "Deactivate Entity", description = "Deactivates the passed Entity Id")
    public ResponseDto<?> deactivate(@Schema(name = "id", description = "Entity Id of type Long", type = "Long", defaultValue = "1") @PathVariable @Valid K id) {
        this.baseService.deactivateById(id);
        log.info("Deactivate Request for the entity in abstract controller ->{}", id);
        return new ResponseDto<>("Data has been deactivated successfully", id);
    }

    @PostMapping({"/activate/{id}"})
    @Operation(summary = "Activate Entity", description = "Activates the passed Entity Id")
    public ResponseDto<?> activate(@Schema(name = "id", description = "Entity Id of type Long", type = "Long", defaultValue = "1") @PathVariable @Valid K id) {
        this.baseService.activateById(id);
        log.info("Activate Request for the entity in abstract controller ->{}", id);
        return new ResponseDto<>("Data has been activated successfully", id);
    }

    @PostMapping({"/soft-delete/{id}"})
    @Operation(summary = "Soft delete Entity", description = "Soft deletes the passed Entity Id")
    public ResponseDto<?> softDelete(@Schema(name = "id", description = "Entity Id of type Long", type = "Long", defaultValue = "1") @PathVariable @Valid K id) {
        this.baseService.softDeleteById(id);
        log.info("Delete Request for the entity in abstract controller ->{}", id);
        return new ResponseDto<>("Data has been deleted successfully", id);
    }

    @PostMapping(value = {"/filtered-list"}, produces = {"application/json"}, consumes = {"application/json"})
    @Operation(summary = "List of Entity", description = "Filtered List of entities on the basis of passed filters")
    public ResponseDto<?> list(@RequestBody @Valid ListRequest listRequest) {
        List<E> list = this.baseService.list(listRequest);
        log.info("List Request for the entity in abstract controller");
        return new ResponseDto<>("Filtered List fetched successfully", list);
    }
}
