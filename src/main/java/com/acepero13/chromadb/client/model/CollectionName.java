package com.acepero13.chromadb.client.model;


import com.acepero13.chromadb.client.utils.StringUtils;

/**
 * Represents a valid name for a collection.
 * This class encapsulates the logic for validating and storing a collection name.
 */
public class CollectionName {
    private final String name;

    private CollectionName(String name) {
        this.name = name;
    }


    /**
     * Creates a new {@link CollectionName} object from the given string.
     *
     * <p>The name must be a valid name, according to the {@link ValidName} class.
     * If the name is not valid, an exception will be thrown with a message indicating the reason why it is invalid.
     *
     * @param name the name of the collection
     * @return a new {@link CollectionName} object
     * @throws RuntimeException if the name is not valid
     */
    public static CollectionName of(String name) {
        ValidName nameValidity = ValidName.validate(name);
        if (!nameValidity.isValid) {
            throw new RuntimeException("Collection name is not valid. Reason: " + nameValidity.errorReason);
        }
        return new CollectionName(name);
    }

    /**
     * Returns a CollectionName of the given object.
     *
     * @param name The name to use for the collection
     * @return A CollectionName of the given object.
     */
    public static CollectionName of(Object name) {
        return of((String) name);
    }

    /**
     * Returns the name of the collection.
     *
     * @return the name of the collection
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a string representation of the collection name.
     *
     * @return The collection name as a string.
     */
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CollectionName that = (CollectionName) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    private static class ValidName {
        private final boolean isValid;
        private final String errorReason;

        private ValidName(boolean isValid, String errorReason) {
            this.isValid = isValid;
            this.errorReason = errorReason;
        }

        /**
         * Checks if the name is valid and returns a ValidName object with the result of the validation.
         *
         * @return ValidName object that holds the result of the validation.
         */
        private static ValidName valid() {
            return new ValidName(true, "");
        }

        /**
         * Returns a ValidName object with isValid set to false and a given reason for invalidity.
         *
         * @param reason The reason why the name is invalid.
         * @return A ValidName object with isValid set to false.
         */
        private static ValidName invalid(String reason) {
            return new ValidName(false, reason);
        }


        /**
         * Validates the given collection name against predefined criteria.
         * <p>
         * The name must be between 3 and 63 characters, start and end with a lowercase letter or a digit,
         * and can only contain lowercase letters, digits, dots, dashes, and underscores.
         * It must not contain consecutive dots or represent a valid IP address.
         *
         * @param name The collection name to validate.
         * @return A ValidName instance representing the validity of the name and the reason if invalid.
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
