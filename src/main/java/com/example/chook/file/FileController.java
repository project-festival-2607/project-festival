package com.example.chook.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
    return "redirect:/test/file";
  }

  @PostMapping("/delete")
  public String delete(@RequestParam String uuid) {
    FileDTO targetFileDtoSkeleton = FileDTO.builder()
        .uuid(uuid)
          .build();
    fileService.delete(targetFileDtoSkeleton);
    return "redirect:/test/file";
  }

}
