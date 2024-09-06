package com.cdac.exambackup.dao.repo;

import com.cdac.exambackup.entity.FileType;
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
public interface FileTypeRepository extends JpaRepository<FileType, Long> {
    List<FileType> findByCodeOrNameIgnoreCaseAndDeletedFalse(String code, String name);

    long countByDeletedFalse();

    @Query("SELECT ft FROM FileType ft WHERE ft.deleted = false ORDER BY CAST(ft.code AS INTEGER) ASC")
    Page<FileType> findByDeletedFalse(Pageable pageable);
}
