package com.example.chook.festival;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FesImageHandler {
    @Value("${file.upload-dir}")
    private String uploadDir;

    public FesImageDTO uploadFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }

        String originalFileName = multipartFile.getOriginalFilename();
        String storeFileName = UUID.randomUUID().toString() + "_" + originalFileName;

        File festivalDir = new File(uploadDir, "festival");
        File saveFile = new File(festivalDir, storeFileName);

        try {
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            multipartFile.transferTo(saveFile);
        } catch (Exception e) {
            throw new RuntimeException("파일 저장에 실패했습니다", e);
        }

        return FesImageDTO.builder()
                .originalFileName(originalFileName)
                .storedFileName(storeFileName)
                .uploadImagePath("/festival/display?fileName=" + storeFileName)
                .fileSize(multipartFile.getSize())
                .build();
    }
}
