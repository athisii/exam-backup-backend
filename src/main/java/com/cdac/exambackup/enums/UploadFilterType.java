package com.cdac.exambackup.enums;

/**
 * @author athisii
 * @version 1.0
 * @since 12/7/24
 */

public enum UploadFilterType {
    DEFAULT("DEFAULT"),
    UPLOADED("UPLOADED"),
    NOT_UPLOADED("NOT_UPLOADED");

    private final String value;

    UploadFilterType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
