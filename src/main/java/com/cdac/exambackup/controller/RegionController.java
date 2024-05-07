package com.cdac.exambackup.controller;

import com.cdac.exambackup.dto.ResponseDto;
import com.cdac.exambackup.entity.Region;
import com.cdac.exambackup.service.BaseService;
import com.cdac.exambackup.service.RegionService;
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

@Tag(name = "Region Controller")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/regions")
public class RegionController extends AbstractBaseController<Region, Long> {
    @Autowired
    RegionService regionService;

    public RegionController(BaseService<Region, Long> baseService) {
        super(baseService);
    }

    @Override
    @PostMapping(value = {"/create"}, produces = {"application/json"}, consumes = {"application/json"})
    public ResponseDto<?> create(@RequestBody @Valid Region region) {
        // TODO:: log user id?
        log.info("Create request for the entity by userId: ");
        return new ResponseDto<>("Your data has been saved successfully", this.regionService.save(region).getId());
    }
}
