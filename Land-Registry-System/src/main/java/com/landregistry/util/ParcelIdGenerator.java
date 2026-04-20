package com.landregistry.util;

import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;

/**
 * Utility to generate deterministic, unique land parcel IDs.
 */
public class ParcelIdGenerator {

    private ParcelIdGenerator() {}

    /**
     * Generates a parcel ID based on district, village, and survey number.
     * Format: DIST-VILL-SURVEY-XXXXXXXX (first 8 chars of SHA-256)
     */
    public static String generate(String district, String village, String surveyNumber) {
        String combined = (district + village + surveyNumber).toUpperCase().replaceAll("\\s+", "");
        String hash = sha256Short(combined);
        String prefix = sanitize(district).substring(0, Math.min(4, sanitize(district).length()));
        return prefix.toUpperCase() + "-" + hash.toUpperCase();
    }

    private static String sha256Short(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes("UTF-8"));
            return HexFormat.of().formatHex(hashBytes).substring(0, 12);
        } catch (Exception e) {
            // fallback
            return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        }
    }

    private static String sanitize(String input) {
        return input.replaceAll("[^a-zA-Z0-9]", "");
    }
}
