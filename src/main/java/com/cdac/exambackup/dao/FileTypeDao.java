package com.cdac.exambackup.dao;

import com.cdac.exambackup.entity.FileType;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface FileTypeDao extends BaseDao<FileType, Long> {
    List<FileType> findByCodeOrName(String code, String name);
}
