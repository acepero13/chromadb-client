package com.acepero13.chromadb.client.model;


import com.acepero13.chromadb.client.utils.StringUtils;

public class CollectionName {
    private final String name;

    private CollectionName(String name) {
        this.name = name;
    }

    public static CollectionName of(String name) {
        ValidName nameValidity = ValidName.validate(name);
        if (!nameValidity.isValid) {
            throw new RuntimeException("Collection name is not valid. Reason: " + nameValidity.errorReason);
        }
        return new CollectionName(name);
    }

    public static CollectionName of(Object name) {
        return of((String) name);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }


    private static class ValidName {
        private final boolean isValid;
        private final String errorReason;

        private ValidName(boolean isValid, String errorReason) {
            this.isValid = isValid;
            this.errorReason = errorReason;
        }

        private static ValidName valid() {
            return new ValidName(true, "");
        }

        private static ValidName invalid(String reason) {
            return new ValidName(false, reason);
        }

        /**
         * The length of the name must be between 3 and 63 characters.
         * The name must start and end with a lowercase letter or a digit, and it can contain dots, dashes, and underscores in between.
         * The name must not contain two consecutive dots.
         * The name must not be a valid IP address.
         */

        private static ValidName validate(String name) {
            if (StringUtils.isNullOrEmpty(name)) {
                return invalid("Name cannot be null or empty");
            }
            if (!StringUtils.lengthIsBetween(name, 3, 63)) {
                return invalid("The length of the name must be between 3 and 63 characters");
            }
            // Check if the name starts and ends with a lowercase letter or a digit
            if (!StringUtils.startsAndEndsWithLowercaseLetterOrDigit(name)) {
                return invalid("Name must start and end with a lowercase or a digit");
            }

            // Check if the name contains consecutive dots
            if (name.contains("..")) {
                return invalid("Name must not contain consecutive dots");
            }
            return valid();
        }


    }
}
