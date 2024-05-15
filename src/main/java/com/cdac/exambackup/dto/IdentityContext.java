package com.cdac.exambackup.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

/**
 * @author athisii
 * @version 1.0
 * @since 5/15/24
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
@RequestScope
public class IdentityContext {
    Long id;
    String userId;
    String name;
    List<Long> permissions;
}
