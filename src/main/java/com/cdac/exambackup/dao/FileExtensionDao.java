package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.FileExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface FileExtensionDao extends BaseDao<FileExtension, Long> {
    List<FileExtension> findByCodeOrName(String code, String name);

    long countNonDeleted();

    Page<FileExtension> getAllByPage(Pageable pageable);
}
