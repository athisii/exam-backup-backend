package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.SlotDao;
import com.cdac.exambackup.dao.repo.SlotRepository;
import com.cdac.exambackup.entity.Slot;
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
public class SlotDaoImpl extends AbstractBaseDao<Slot, Long> implements SlotDao {
    private static final String ERROR_MSG = "Invalid sorting field name or sorting direction. Must be sort:['fieldName,asc','fieldName,desc']";

    @Autowired
    SlotRepository slotRepository;

    @Override
    public JpaRepository<Slot, Long> getRepository() {
        return this.slotRepository;
    }

    @Override
    public Class<Slot> getEntityClass() {
        return Slot.class;
    }

    @Override
    public void softDelete(Slot entity) {
        if (entity != null) {
            entity.setDeleted(true);
            entity.setCode("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + entity.getCode());
            entity.setName("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + entity.getName());
            slotRepository.save(entity);
        }
    }

    @Override
    public void softDelete(Collection<Slot> entities) {
        if (entities != null && !entities.isEmpty()) {
            entities.forEach(entity -> {
                entity.setDeleted(true);
                entity.setCode("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + entity.getCode());
                entity.setName("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + entity.getName());
            });
            slotRepository.saveAll(entities);
        }
    }

    @Override
    public List<Slot> findByCodeOrName(String code, String name) {
        return this.slotRepository.findByCodeOrNameIgnoreCaseAndDeletedFalse(code, name);
    }

    @Override
    public Page<Slot> getAllByPage(Pageable pageable) {
        try {
            return this.slotRepository.findByDeletedFalse(pageable);
        } catch (PropertyReferenceException ex) {
            throw new GenericException(ERROR_MSG);
        }
    }
}
