package org.example.grocery_app.util;

//package com.yourpackage.util;

import java.security.SecureRandom;

public class CodeGenerator {

    private static final String PREFIX = "cash_";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SUFFIX_LENGTH = 10;
    private static final SecureRandom random = new SecureRandom();

    public static String generateCashCode() {
        StringBuilder suffix = new StringBuilder(SUFFIX_LENGTH);
        for (int i = 0; i < SUFFIX_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            suffix.append(CHARACTERS.charAt(index));
        }
        return PREFIX + suffix;
    }
}
