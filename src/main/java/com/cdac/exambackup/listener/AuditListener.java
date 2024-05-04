package com.cdac.exambackup.listener;

import com.cdac.exambackup.entity.AuditModel;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import java.util.Date;

/**
 * @author athisii
 * @version 1.0
 * @since 5/3/24
 */

public class AuditListener {

    @PrePersist
    protected void beforePersist(AuditModel auditModel) {
        auditModel.setCreatedDate(new Date());
        auditModel.setModifiedDate(new Date());
    }

    @PreUpdate
    protected void beforeUpdate(AuditModel auditModel) {
        auditModel.setModifiedDate(new Date());
    }
}
