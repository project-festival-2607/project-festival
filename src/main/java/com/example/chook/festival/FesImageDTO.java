package com.example.chook.festival;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FesImageDTO {
    private String originalFileName;
    private String storedFileName;
    private String uploadImagePath;
    private long fileSize;
}
