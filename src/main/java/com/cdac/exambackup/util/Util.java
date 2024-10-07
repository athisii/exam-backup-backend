package com.cdac.exambackup.util;

import com.cdac.exambackup.exception.InvalidReqPayloadException;

import java.util.Random;
import java.util.regex.Pattern;

/**
 * @author athisii
 * @version 1.0
 * @since 5/11/24
 */

public class Util {
    private static final String ALPHANUMERICS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random random = new Random();

    private Util() {

    }

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static final Pattern VALID_MOBILE_NUMBER_REGEX =
            Pattern.compile("^(\\+91[\\-\\s]?)?[0]?(91)?[789]\\d{9}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmail(String email) {
        return VALID_EMAIL_ADDRESS_REGEX.matcher(email).matches();
    }

    public static boolean validateMobileNumber(String mobileNumber) {
        return VALID_MOBILE_NUMBER_REGEX.matcher(mobileNumber).matches();
    }

    public static void isConvertibleToNumberElseThrowException(String fieldName, String value) {
        try {
            Integer.parseInt(value);
        } catch (Exception e) {
            throw new InvalidReqPayloadException("'" + fieldName + "' must be a number.");
        }
    }

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(ALPHANUMERICS.charAt(random.nextInt(ALPHANUMERICS.length())));
        }
        return sb.toString();
    }
}
