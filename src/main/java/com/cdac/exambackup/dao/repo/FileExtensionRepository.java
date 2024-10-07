package com.cdac.exambackup.dao.repo;

import com.cdac.exambackup.entity.FileExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Repository
public interface FileExtensionRepository extends JpaRepository<FileExtension, Long> {
    List<FileExtension> findByCodeOrNameIgnoreCaseAndDeletedFalse(String code, String name);

    long countByDeletedFalse();

    @Query("SELECT fx FROM FileExtension fx WHERE fx.deleted = false ORDER BY CAST(fx.code AS INTEGER) ASC")
    Page<FileExtension> findByDeletedFalse(Pageable pageable);
}
