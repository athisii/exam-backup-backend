package com.cdac.exambackup.dao.impl;

import com.cdac.exambackup.dao.SlotDao;
import com.cdac.exambackup.dao.repo.ExamSlotRepository;
import com.cdac.exambackup.dao.repo.SlotRepository;
import com.cdac.exambackup.entity.Slot;
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
public class SlotDaoImpl extends AbstractBaseDao<Slot, Long> implements SlotDao {
    private static final String ERROR_MSG = "Invalid sorting field name or sorting direction. Must be sort:['fieldName,asc','fieldName,desc']";


    @Autowired
    SlotRepository slotRepository;

    @Autowired
    ExamSlotRepository examSlotRepository;

    @Override
    public JpaRepository<Slot, Long> getRepository() {
        return this.slotRepository;
    }

    @Override
    public Class<Slot> getEntityClass() {
        return Slot.class;
    }

    private void markDeletedAndAddSuffix(Slot slot) {
        if (examSlotRepository.existsBySlotIdAndDeletedFalse(slot.getId())) {
            throw new InvalidReqPayloadException("Slot code: " + slot.getCode() + " is associated with some exams. Cannot delete it.");
        }
        slot.setDeleted(true);
        // user should be allowed to add same name after deleted
        // add suffix to avoid unique constraint violation for code
        slot.setCode("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + slot.getCode());
        // add suffix to avoid unique constraint violation for name
        slot.setName("_deleted_" + new Date().toInstant().getEpochSecond() + "_" + slot.getName());
        // add nanoseconds to start and end time to avoid unique constraint violation
        slot.setStartTime(slot.getStartTime().plusNanos(slot.getId().intValue()));
        slot.setEndTime(slot.getEndTime().plusNanos(slot.getId().intValue()));
    }

    @Override
    public void softDelete(Slot slot) {
        markDeletedAndAddSuffix(slot);
        slotRepository.save(slot);
    }

    @Override
    public void softDelete(Collection<Slot> slots) {
        if (slots != null && !slots.isEmpty()) {
            slots.forEach(this::markDeletedAndAddSuffix);
            slotRepository.saveAll(slots);
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
        } catch (Exception ex) {
            throw new InvalidReqPayloadException(ERROR_MSG);
        }
    }
}
