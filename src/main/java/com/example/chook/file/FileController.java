package com.example.chook.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/file")
@RequiredArgsConstructor
@Slf4j
public class FileController {

  private final FileService fileService;

  @PostMapping("/upload")
  public String upload(@RequestParam(name="file",  required=true) MultipartFile file) {
    FileDTO uploadedFileDto = fileService.uploadAndGetDto(file);
    log.info("uploadedFileDto: {}", uploadedFileDto);
    return "test/file";
  }

}
