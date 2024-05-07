package com.cdac.exambackup.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

/**
 * @author athisii
 * @version 1.0
 * @since 5/5/24
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "List Request based on applied filters")
public class ListRequest {
    @Schema(description = "offset of request", requiredMode = Schema.RequiredMode.REQUIRED)
    @Builder.Default
    int start = 0;

    @Schema(description = "limit of request", requiredMode = Schema.RequiredMode.REQUIRED)
    @Builder.Default
    int length = 20;

    @Schema(description = "search keyword")
    String searchTag;
    @Schema(description = "sort property")

    @Builder.Default
    String sortBy = "id";
    @Schema(description = "sort order")

    @Builder.Default
    String sortOrder = "asc";
    @Schema(description = "Filters")
    Map<String, List<Object>> filters;
}
