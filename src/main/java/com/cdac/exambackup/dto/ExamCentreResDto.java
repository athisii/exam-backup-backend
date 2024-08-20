package com.cdac.exambackup.dto;

/**
 * @author athisii
 * @version 1.0
 * @since 5/12/24
 */

public record ExamCentreResDto(Long id, String code, String name, String regionName, Integer totalFileCount,
                               Integer uploadedFileCount) {
}
