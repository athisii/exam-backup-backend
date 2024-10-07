package com.cdac.exambackup.service;

import com.cdac.exambackup.dto.PageResDto;
import com.cdac.exambackup.entity.FileExtension;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/6/24
 */

public interface FileExtensionService extends BaseService<FileExtension, Long> {
    PageResDto<List<FileExtension>> getAllByPage(Pageable pageable);
}
