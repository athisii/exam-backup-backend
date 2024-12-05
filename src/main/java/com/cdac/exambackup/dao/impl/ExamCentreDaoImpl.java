package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.AppUserDao;
import com.cdac.exambackup.dao.ExamCentreDao;
import com.cdac.exambackup.dao.repo.ExamCentreRepository;
import com.cdac.exambackup.dao.repo.ExamRepository;
import com.cdac.exambackup.dao.repo.ExamSlotRepository;
import com.cdac.exambackup.entity.AppUser;
import com.cdac.exambackup.entity.ExamCentre;
import com.cdac.exambackup.exception.InvalidReqPayloadException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;

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

    @Autowired
    AppUserDao appUserDao;

    @Autowired
    ExamRepository examRepository;

    @Autowired
    ExamSlotRepository examSlotRepository;

    @Override
    public JpaRepository<ExamCentre, Long> getRepository() {
        return this.examCentreRepository;
    }

    @Override
    public Class<ExamCentre> getEntityClass() {
        return ExamCentre.class;
    }

    private void markDeletedAndAddSuffixAndRemoveAppUser(ExamCentre examCentre) {
        AppUser appUser = appUserDao.findByUserId(examCentre.getCode());
        if (appUser == null) {
            throw new InvalidReqPayloadException("User with userId: " + examCentre.getCode() + " not found.");
        }
        // delete examCentre-user map from app_user table.
        appUserDao.delete(appUser);


        // find and delete related records in exam and exam_slot tables.
        List<Long> examIds = examRepository.findIdsByExamCentreId(examCentre.getId());
        examSlotRepository.deleteByExamIdIn(examIds);
        examRepository.deleteByIdIn(examIds);

        // finally, mark this exam centre as deleted
        examCentre.setDeleted(true);
        // user should be allowed to add same name after deleted
        // add suffix to avoid unique constraint violation for code
        examCentre.setCode("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + examCentre.getCode());
        // add suffix to avoid unique constraint violation for name
        examCentre.setName("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + examCentre.getName());
    }

    @Transactional
    @Override
    public void softDelete(ExamCentre examCentre) {
        markDeletedAndAddSuffixAndRemoveAppUser(examCentre);
        examCentreRepository.save(examCentre);
    }

    @Transactional
    @Override
    public void softDelete(Collection<ExamCentre> examCentres) {
        if (examCentres != null && !examCentres.isEmpty()) {
            examCentres.forEach(this::markDeletedAndAddSuffixAndRemoveAppUser);
            examCentreRepository.saveAll(examCentres);
        }
    }

    @Override
    public ExamCentre findByCode(String code) {
        return this.examCentreRepository.findFirstByCodeAndDeletedFalse(code);
    }

    @Override
    public Page<ExamCentre> findByCode(String code, Pageable pageable) {
        try {
            return this.examCentreRepository.findByCodeIgnoreCaseAndDeletedFalse(code, pageable);
        } catch (Exception ex) {
            throw new InvalidReqPayloadException(ERROR_MSG);
        }
    }

    @Override
    public ExamCentre findByCodeAndName(String code, String name) {
        return this.examCentreRepository.findFirstByCodeAndNameIgnoreCaseAndDeletedFalse(code, name);
    }

    @Override
    public Page<ExamCentre> findByCodeAndName(String code, String name, Pageable pageable) {
        try {
            return this.examCentreRepository.findByCodeAndNameIgnoreCaseAndDeletedFalse(code, name, pageable);
        } catch (Exception ex) {
            throw new InvalidReqPayloadException(ERROR_MSG);
        }
    }

    @Override
    public Page<ExamCentre> findByRegionIdAndCodeAndName(Long regionId, String code, String name, Pageable pageable) {
        try {
            return this.examCentreRepository.findByRegionIdAndCodeAndNameIgnoreCaseAndDeletedFalse(regionId, code, name, pageable);
        } catch (Exception ex) {
            throw new InvalidReqPayloadException(ERROR_MSG);
        }
    }

    @Override
    public Page<ExamCentre> findByName(String name, Pageable pageable) {
        try {
            return this.examCentreRepository.findByNameIgnoreCaseAndDeletedFalse(name, pageable);
        } catch (Exception ex) {
            throw new InvalidReqPayloadException(ERROR_MSG);
        }
    }

    @Override
    public Page<ExamCentre> findByRegionId(Long regionId, Pageable pageable) {
        try {
            return this.examCentreRepository.findByRegionIdAndDeletedFalse(regionId, pageable);
        } catch (Exception ex) {
            throw new InvalidReqPayloadException(ERROR_MSG);
        }
    }

    @Override
    public Page<ExamCentre> searchWithRegionId(String query, Long regionId, Pageable pageable) {
        try {
            return this.examCentreRepository.findByRegionIdAndSearchTermAndDeletedFalse(regionId, query, pageable);
        } catch (Exception ex) {
            throw new InvalidReqPayloadException(ERROR_MSG);
        }
    }

    @Override
    public Page<ExamCentre> search(String query, Pageable pageable) {
        try {
            return this.examCentreRepository.findBySearchTermAndDeletedFalse(query, pageable);
        } catch (Exception ex) {
            throw new InvalidReqPayloadException(ERROR_MSG);
        }
    }

    @Override
    public Page<ExamCentre> findByRegionIdAndCode(Long regionId, String code, Pageable pageable) {
        try {
            return this.examCentreRepository.findByRegionIdAndCodeAndDeletedFalse(regionId, code, pageable);
        } catch (Exception ex) {
            throw new InvalidReqPayloadException(ERROR_MSG);
        }
    }

    @Override
    public Page<ExamCentre> findByRegionIdAndName(Long regionId, String name, Pageable pageable) {
        try {
            return this.examCentreRepository.findByRegionIdAndNameIgnoreCaseAndDeletedFalse(regionId, name, pageable);
        } catch (Exception ex) {
            throw new InvalidReqPayloadException(ERROR_MSG);
        }
    }

    @Override
    public List<ExamCentre> findByRegionId(Long regionId) {
        return this.examCentreRepository.findByRegionIdAndDeletedFalse(regionId);
    }

    @Override
    public Page<ExamCentre> getAllByPage(Pageable pageable) {
        try {
            return this.examCentreRepository.findByDeletedFalse(pageable);
        } catch (Exception ex) {
            throw new InvalidReqPayloadException(ERROR_MSG);
        }
    }

    @Override
    public List<ExamCentre> getAllByRegionIds(List<Long> regionIds) {
        return this.examCentreRepository.getAllByRegionIdInAndDeletedFalse(regionIds);

    }
}
