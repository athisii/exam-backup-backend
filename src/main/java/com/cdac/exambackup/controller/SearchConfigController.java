package com.cdac.exambackup.controller;

import com.cdac.exambackup.entity.SearchConfig;
import com.cdac.exambackup.service.BaseService;
import com.cdac.exambackup.service.SearchConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author athisii
 * @version 1.0
 * @since 5/5/24
 */

@Tag(name = "SearchConfig Controller")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/search-configs")
public class SearchConfigController extends AbstractBaseController<SearchConfig, Long> {
    @Autowired
    SearchConfigService searchConfigService;

    public SearchConfigController(BaseService<SearchConfig, Long> service) {
        super(service);
    }
}
