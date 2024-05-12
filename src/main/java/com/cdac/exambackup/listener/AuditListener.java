package com.cdac.exambackup.listener;

import com.cdac.exambackup.entity.AuditModel;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.time.LocalDateTime;

/**
 * @author athisii
 * @version 1.0
 * @since 5/3/24
 */

public class AuditListener {

    @PrePersist
    protected void beforePersist(AuditModel auditModel) {
        auditModel.setCreatedDate(LocalDateTime.now());
        auditModel.setModifiedDate(LocalDateTime.now());
    }

    @PreUpdate
    protected void beforeUpdate(AuditModel auditModel) {
        auditModel.setModifiedDate(LocalDateTime.now());
    }
}
