package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.ExamCentre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */


public interface ExamCentreDao extends BaseDao<ExamCentre, Long> {
    ExamCentre findByCode(String code);

    Page<ExamCentre> findByName(String name, Pageable pageable);

    ExamCentre findByCodeAndName(String code, String name);

    Page<ExamCentre> findByCodeOrName(String code, String name, Pageable pageable);

    Page<ExamCentre> findByRegionIdAndCodeOrName(Long regionId, String code, String name, Pageable pageable);

    Page<ExamCentre> findByRegionId(Long regionId, Pageable pageable);

    Page<ExamCentre> queryWithRegionId(String query, Long regionId, Pageable pageable);
    Page<ExamCentre> query(String query, Pageable pageable);

    Page<ExamCentre> findByRegionIdAndCode(Long regionId, String code, Pageable pageable);

    Page<ExamCentre> findByRegionIdAndName(Long regionId, String name, Pageable pageable);
}
