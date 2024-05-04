package com.cdac.exambackup.controller;

import com.cdac.exambackup.entity.ExamSlot;
import com.cdac.exambackup.service.BaseService;
import com.cdac.exambackup.service.ExamSlotService;
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
 * @since 5/6/24
 */

@Tag(name = "Exam Slot Controller")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequestMapping("/exam-slots")
public class ExamSlotController extends AbstractBaseController<ExamSlot, Long> {
    @Autowired
    ExamSlotService examSlotService;

    public ExamSlotController(BaseService<ExamSlot, Long> baseService) {
        super(baseService);
    }
}
