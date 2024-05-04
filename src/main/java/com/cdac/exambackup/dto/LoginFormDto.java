package com.cdac.exambackup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * @author athisii
 * @version 1.0
 * @since 5/4/24
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginFormDto {
    @NotBlank
    String username;

    @NotBlank
    @Pattern(regexp = "\\.{8,20}") // add more complex password combination
    String password;
}
