package com.cdac.exambackup.controller;

import com.cdac.exambackup.dto.ResponseDto;
import com.cdac.exambackup.entity.FileType;
import com.cdac.exambackup.service.BaseService;
import com.cdac.exambackup.service.FileTypeService;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "File Type Controller")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/file-types")
public class FileTypeController extends AbstractBaseController<FileType, Long> {
    @Autowired
    FileTypeService fileTypeService;

    public FileTypeController(BaseService<FileType, Long> baseService) {
        super(baseService);
    }

    @PostMapping(value = {"/create"}, produces = {"application/json"}, consumes = {"application/json"})
    @Operation(summary = "Create/Update entity", description = "Create or Update (if Id passed) the entity in Database")
    public ResponseDto<?> create(@RequestBody @Valid FileType fileType) {
        // TODO:: log user id?
        log.info("Create request for the entity by userId: ");
        return new ResponseDto<>("Your data has been saved successfully", this.fileTypeService.save(fileType).getId());
    }
}
