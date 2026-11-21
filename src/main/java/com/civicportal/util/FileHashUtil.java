package com.civicportal.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class FileHashUtil {

    private static final int BUFFER_SIZE = 8192;

    private FileHashUtil() {
    }

    public static String sha256(byte[] content) {
        if (content == null) {
            throw new IllegalArgumentException("File content must not be null");
        }

        return HexFormat.of().formatHex(newDigest().digest(content));
    }

    public static String sha256(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream must not be null");
        }

        MessageDigest digest = newDigest();
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            digest.update(buffer, 0, bytesRead);
        }

        return HexFormat.of().formatHex(digest.digest());
    }

    private static MessageDigest newDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
