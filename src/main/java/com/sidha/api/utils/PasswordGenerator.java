package com.sidha.api.utils;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class PasswordGenerator {

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String DIGIT = "0123456789";
    private static final String SPECIAL_CHAR = "!@#$%&*()_+-=[]?";

    private static final String PASSWORD_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + DIGIT + SPECIAL_CHAR;

    private static SecureRandom random = new SecureRandom();

    public static String generatePassword(int length) {
        if (length < 4) {
            throw new IllegalArgumentException("Password length must be at least 4 characters");
        }

        StringBuilder sb = new StringBuilder(length);
        // at least one lower-case character
        sb.append(CHAR_LOWER.charAt(random.nextInt(CHAR_LOWER.length())));
        // at least one upper-case character
        sb.append(CHAR_UPPER.charAt(random.nextInt(CHAR_UPPER.length())));
        // at least one digit character
        sb.append(DIGIT.charAt(random.nextInt(DIGIT.length())));
        // at least one special character
        sb.append(SPECIAL_CHAR.charAt(random.nextInt(SPECIAL_CHAR.length())));

        for (int i = 4; i < length; i++) {
            sb.append(PASSWORD_ALLOW_BASE.charAt(random.nextInt(PASSWORD_ALLOW_BASE.length())));
        }

        return shuffleString(sb.toString());
    }

    private static String shuffleString(String string) {
        char[] charArray = string.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            int randomIndex = random.nextInt(charArray.length);
            char temp = charArray[i];
            charArray[i] = charArray[randomIndex];
            charArray[randomIndex] = temp;
        }
        return new String(charArray);
    }
}