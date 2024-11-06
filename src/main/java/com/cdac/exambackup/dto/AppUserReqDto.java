package com.cdac.exambackup.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;

/**
 * @author athisii
 * @version 1.0
 * @since 5/12/24
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AppUserReqDto(Long id, String userId, String name, String mobileNumber, String email, Boolean isRegionHead, Long regionId, Long roleId) {
}
