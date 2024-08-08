package com.cdac.exambackup.dto;

/**
 * @author athisii
 * @version 1.0
 * @since 8/8/24
 */

public record PageResDto<T>(int page, int totalPages, T items) {
}
