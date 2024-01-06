package com.acepero13.chromadb.client.utils;

import java.util.regex.Pattern;

public class StringUtils {
    private StringUtils() {

    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isBlank();
    }

    public static boolean lengthIsBetween(String name, int lowerBound, int upperBound) {
        return name.length() >= lowerBound && name.length() <= upperBound;
    }

    public static boolean startsAndEndsWithLowercaseLetterOrDigit(String name) {
        return Pattern.matches("^[a-z0-9].*[a-z0-9]$", name);
    }


}
