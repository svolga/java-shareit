package ru.practicum.shareit.util;

public class Validation {

    public static boolean stringIsNotNullOrBlank(String string) {
        return string != null && !string.isBlank();
    }

    public static boolean validEmail(String email) {
        return !email.contains(" ") && email.contains("@");
    }

    public static boolean objectIsNotNull(Object object) {
        return object != null;
    }
}
