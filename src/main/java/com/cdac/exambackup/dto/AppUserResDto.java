package com.cdac.exambackup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * @author athisii
 * @version 1.0
 * @since 5/12/24
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AppUserResDto(Long id, String userId, String name, Long regionId, String mobileNumber, String email,
                            Long roleId, boolean isRegionHead,
                            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm") LocalDateTime createdDate,
                            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm") LocalDateTime modifiedDate) {
}
