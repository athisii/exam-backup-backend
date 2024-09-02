package com.cdac.exambackup.util;

import java.time.LocalTime;

/**
 * @author athisii
 * @version 1.0
 * @since 9/1/24
 */

public class NullAndBlankUtil {
    private NullAndBlankUtil() {
    }

    public static boolean isAnyNullOrBlank(String... args) {
        for (String arg : args) {
            if (arg == null || arg.isBlank()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAllNullOrBlank(String... args) {
        int count = args.length;
        for (String arg : args) {
            if (arg == null || arg.isBlank()) {
                count--;
            }
        }
        return count == 0;
    }
    public static boolean isAllNull(LocalTime... args) {
        int count = args.length;
        for (LocalTime arg : args) {
            if (arg == null) {
                count--;
            }
        }
        return count == 0;
    }
}
