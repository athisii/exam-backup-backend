package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.FileType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface FileTypeDao extends BaseDao<FileType, Long> {
    List<FileType> findByCodeOrName(String code, String name);

    long countNonDeleted();

    Page<FileType> getAllByPage(Pageable pageable);
}
