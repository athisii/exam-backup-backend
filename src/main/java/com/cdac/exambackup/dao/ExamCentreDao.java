package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.entity.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */


public interface ExamCentreDao extends BaseDao<ExamCentre, Long> {
    ExamCentre findByCode(String code);

    ExamCentre findByCodeAndName(String code, String name);

    ExamCentre findByName(String name);

    Page<ExamCentre> findByRegion(Region region, Pageable pageable);
}
