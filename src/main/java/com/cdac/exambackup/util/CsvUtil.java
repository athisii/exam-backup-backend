package com.cdac.exambackup.util;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author athisii
 * @version 1.0
 * @since 10/2/24
 */

public class CsvUtil {
    public static final String TYPE = "text/csv";

    private CsvUtil() {
    }

    public static boolean hasCsvFormat(MultipartFile file) {
        return !file.isEmpty() && TYPE.equals(file.getContentType());
    }
}
