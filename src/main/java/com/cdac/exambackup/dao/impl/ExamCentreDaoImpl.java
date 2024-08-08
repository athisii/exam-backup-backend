package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.ExamCentreDao;
import com.cdac.exambackup.dao.repo.ExamCentreRepository;
import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.entity.Region;
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
        return this.examCentreRepository.findFirstByCodeIgnoreCase(code);
    }

    @Override
    public ExamCentre findByCodeAndName(String code, String name) {
        return this.examCentreRepository.findFirstByCodeAndName(code, name);
    }

    @Override
    public ExamCentre findByName(String name) {
        return this.examCentreRepository.findFirstByName(name);
    }

    @Override
    public Page<ExamCentre> findByRegion(Region region, Pageable pageable) {
        try {
            return this.examCentreRepository.findByRegion(region, pageable);
        } catch (PropertyReferenceException ex) {
            throw new GenericException("Invalid sorting field name or sorting direction. Must be sort:['fieldName,asc','fieldName,desc']");
        }
    }

}
