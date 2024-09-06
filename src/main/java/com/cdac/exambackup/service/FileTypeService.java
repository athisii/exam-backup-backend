package com.cdac.exambackup.service;

import com.cdac.exambackup.dto.PageResDto;
import com.cdac.exambackup.entity.FileType;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface FileTypeService extends BaseService<FileType, Long> {
    PageResDto<List<FileType>> getAllByPage(Pageable pageable);
}
