package com.studio.booking.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.studio.booking.exceptions.exceptions.ImageException;
import com.studio.booking.services.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;
    private final List<String> allowedContentTypes = Arrays.asList("image/jpeg", "image/png");

    @Override
    public String uploadImage(MultipartFile file) throws IOException {
        validateFile(file);

        String publicValue = generatePublicValue(file.getOriginalFilename());
        String extension = getFilename(Objects.requireNonNull(file.getOriginalFilename()))[1];
        File fileUpload = convert(file);

        cloudinary.uploader().upload(fileUpload, ObjectUtils.asMap("public_id", publicValue));
        String filePath = cloudinary.url().generate(StringUtils.join(publicValue, ".", extension));
        cleanDisk(fileUpload);

        return filePath;
    }

    private File convert(MultipartFile file) throws IOException {
        // nam + png => nampng
        File convertedFile = new File(StringUtils.join(
                generatePublicValue(file.getOriginalFilename()),
                getFilename(Objects.requireNonNull(file.getOriginalFilename()))[1]
        ));

        // Read file content and copy it to convertedFile
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, convertedFile.toPath());
        }

        return convertedFile;
    }

    // nam.png => jlkfdsajlkjel_nam
    private String generatePublicValue(String originalFilename) {
        String fileName = getFilename(originalFilename)[0];
        return StringUtils.join(StringUtils.join(UUID.randomUUID().toString(), "-", fileName));
    }

    // nam.png => [1]:nam / [2]:png
    private String[] getFilename(String originFilename) {
        return originFilename.split("\\.");
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getOriginalFilename() == null) {
            throw new ImageException("File is null or empty");
        }

        if (!allowedContentTypes.contains(file.getContentType())) {
            throw new ImageException("Content type is not supported");
        }
    }

    private void cleanDisk(File file) {
        try {
            Path filePath = file.toPath();
            Files.delete(filePath);
        } catch (Exception e) {
            throw new ImageException(e.getMessage());
        }
    }

}
