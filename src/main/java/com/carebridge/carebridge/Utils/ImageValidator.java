package com.carebridge.carebridge.Utils;

import org.springframework.web.multipart.MultipartFile;

public class ImageValidator {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    public static void validate(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Profile picture is required");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    "Profile picture must be less than 5MB"
            );
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !(contentType.equals("image/jpeg")
                        || contentType.equals("image/png")
                        || contentType.equals("image/webp"))) {

            throw new IllegalArgumentException(
                    "Only JPG, PNG and WEBP images are allowed"
            );
        }
    }
}
