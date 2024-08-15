package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.ExamCentreDao;
import com.cdac.exambackup.dao.repo.ExamCentreRepository;
import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.exception.GenericException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ExamCentreDaoImpl extends AbstractBaseDao<ExamCentre, Long> implements ExamCentreDao {
    private static final String ERROR_MSG = "Invalid sorting field name or sorting direction. Must be sort:['fieldName,asc','fieldName,desc']";

    @Autowired
    ExamCentreRepository examCentreRepository;

    @Override
    public JpaRepository<ExamCentre, Long> getRepository() {
        return this.examCentreRepository;
    }

    @Override
    public Class<ExamCentre> getEntityClass() {
        return ExamCentre.class;
    }

    @Override
    public ExamCentre findByCode(String code) {
        return this.examCentreRepository.findFirstByCode(code);
    }

    @Override
    public ExamCentre findByCodeAndName(String code, String name) {
        return this.examCentreRepository.findFirstByCodeAndNameIgnoreCase(code, name);
    }

    @Override
    public Page<ExamCentre> findByCodeOrName(String code, String name, Pageable pageable) {
        try {
            return this.examCentreRepository.findByCodeOrNameIgnoreCase(code, name, pageable);
        } catch (PropertyReferenceException ex) {
            throw new GenericException(ERROR_MSG);
        }
    }

    @Override
    public Page<ExamCentre> findByRegionIdAndCodeOrName(Long regionId, String code, String name, Pageable pageable) {
        try {
            return this.examCentreRepository.findByRegionIdAndCodeOrNameIgnoreCase(regionId, code, name, pageable);
        } catch (PropertyReferenceException ex) {
            throw new GenericException(ERROR_MSG);
        }
    }

    @Override
    public Page<ExamCentre> findByName(String name, Pageable pageable) {
        try {
            return this.examCentreRepository.findByNameIgnoreCase(name, pageable);
        } catch (PropertyReferenceException ex) {
            throw new GenericException(ERROR_MSG);
        }
    }

    @Override
    public Page<ExamCentre> findByRegionId(Long regionId, Pageable pageable) {
        try {
            return this.examCentreRepository.findByRegionId(regionId, pageable);
        } catch (PropertyReferenceException ex) {
            throw new GenericException(ERROR_MSG);
        }
    }

    @Override
    public Page<ExamCentre> queryWithRegionId(String query, Long regionId, Pageable pageable) {
        try {
            return this.examCentreRepository.findByRegionIdAndQueryString(regionId, query, pageable);
        } catch (PropertyReferenceException ex) {
            throw new GenericException(ERROR_MSG);
        }
    }

    @Override
    public Page<ExamCentre> query(String query, Pageable pageable) {
        try {
            return this.examCentreRepository.findByQuery(query, pageable);
        } catch (PropertyReferenceException ex) {
            throw new GenericException(ERROR_MSG);
        }
    }

    @Override
    public Page<ExamCentre> findByRegionIdAndCode(Long regionId, String code, Pageable pageable) {
        try {
            return this.examCentreRepository.findByRegionIdAndCode(regionId, code, pageable);
        } catch (PropertyReferenceException ex) {
            throw new GenericException(ERROR_MSG);
        }
    }

    @Override
    public Page<ExamCentre> findByRegionIdAndName(Long regionId, String name, Pageable pageable) {
        try {
            return this.examCentreRepository.findByRegionIdAndNameIgnoreCase(regionId, name, pageable);
        } catch (PropertyReferenceException ex) {
            throw new GenericException(ERROR_MSG);
        }
    }
}
