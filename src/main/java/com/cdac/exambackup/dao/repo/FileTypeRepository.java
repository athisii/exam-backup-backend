package com.cdac.exambackup.dao.repo;

import com.cdac.exambackup.entity.FileType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

@Repository
public interface FileTypeRepository extends JpaRepository<FileType, Long> {
    List<FileType> findByCodeOrNameIgnoreCase(Integer code, String name);
}
