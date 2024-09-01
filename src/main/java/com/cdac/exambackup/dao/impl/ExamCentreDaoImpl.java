package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.AppUserDao;
import com.cdac.exambackup.dao.ExamCentreDao;
import com.cdac.exambackup.dao.repo.ExamCentreRepository;
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

    @Override
    public JpaRepository<ExamCentre, Long> getRepository() {
        return this.examCentreRepository;
    }

    @Override
    public Class<ExamCentre> getEntityClass() {
        return ExamCentre.class;
    }

    @Override
    public void softDelete(ExamCentre entity) {
        if (entity != null) {
            AppUser appUser = appUserDao.findByUserId(entity.getCode());
            appUser.setDeleted(true);
            appUser.setActive(false);

            entity.setDeleted(true);
            entity.setCode("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + entity.getCode());
            entity.setName("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + entity.getName());

            appUser.setUserId(entity.getCode());
            appUser.setName(entity.getName());
            appUserDao.save(appUser);

            examCentreRepository.save(entity);
        }
    }

    @Override
    public void softDelete(Collection<ExamCentre> entities) {
        if (entities != null && !entities.isEmpty()) {
            entities.forEach(entity -> {
                AppUser appUser = appUserDao.findByUserId(entity.getCode());
                appUser.setDeleted(true);
                appUser.setActive(false);

                entity.setDeleted(true);
                entity.setCode("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + entity.getCode());
                entity.setName("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + entity.getName());

                appUser.setUserId(entity.getCode());
                appUser.setName(entity.getName());
                appUserDao.save(appUser);
            });
            examCentreRepository.saveAll(entities);
        }
    }

    @Override
    public ExamCentre findByCode(String code) {
        return this.examCentreRepository.findFirstByCodeAndDeletedFalse(code);
    }

    @Override
    public ExamCentre findByCodeAndName(String code, String name) {
        return this.examCentreRepository.findFirstByCodeAndNameIgnoreCaseAndDeletedFalse(code, name);
    }

    @Override
    public Page<ExamCentre> findByCodeOrName(String code, String name, Pageable pageable) {
        try {
            return this.examCentreRepository.findByCodeOrNameIgnoreCaseAndDeletedFalse(code, name, pageable);
        } catch (Exception ex) {
            throw new InvalidReqPayloadException(ERROR_MSG);
        }
    }

    @Override
    public Page<ExamCentre> findByRegionIdAndCodeOrName(Long regionId, String code, String name, Pageable pageable) {
        try {
            return this.examCentreRepository.findByRegionIdAndCodeOrNameIgnoreCaseAndDeletedFalse(regionId, code, name, pageable);
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
    public Page<ExamCentre> searchWithRegionId(String searchTerm, Long regionId, Pageable pageable) {
        try {
            return this.examCentreRepository.findByRegionIdAndSearchTermAndDeletedFalse(regionId, searchTerm, pageable);
        } catch (Exception ex) {
            throw new InvalidReqPayloadException(ERROR_MSG);
        }
    }

    @Override
    public Page<ExamCentre> search(String searchTerm, Pageable pageable) {
        try {
            return this.examCentreRepository.findBySearchTermAndDeletedFalse(searchTerm, pageable);
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
}
