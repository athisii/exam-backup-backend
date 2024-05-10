package com.cdac.exambackup.util;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * @author athisii
 * @version 1.0
 * @since 5/11/24
 */

public class JsonNodeUtil {
    private JsonNodeUtil() {
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @JsonFilter("conditionalFilter")
    private static class ConditionalFilter {
    }

    public static <T> JsonNode getJsonNode(SimpleBeanPropertyFilter simpleBeanPropertyFilter, T data) {
        objectMapper.addMixIn(Object.class, ConditionalFilter.class); // very important.
        objectMapper.setFilterProvider(new SimpleFilterProvider().addFilter("conditionalFilter", simpleBeanPropertyFilter));
        return objectMapper.valueToTree(data);
    }
}
