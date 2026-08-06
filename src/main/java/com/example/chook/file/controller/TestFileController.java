package com.example.chook.file.controller;

import com.example.chook.file.dto.FileDTO;
import com.example.chook.file.record.FileResource;
import com.example.chook.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/test/file")
@RequiredArgsConstructor
@Slf4j
public class TestFileController {

  private static final String RELATIVE_PATH = "test";
  private final FileService fileService;

  @GetMapping
  public void file(Model model) {

    List<FileDTO> fileDtoList = fileService.getList();
    log.info("fileDtoList: {}", fileDtoList);
    model.addAttribute("fileList", fileDtoList);

  }

  @GetMapping("/{uuid}")
  public ResponseEntity<Resource> getFile(@PathVariable UUID uuid) {
    FileResource file = fileService.getFile(uuid);
    return ResponseEntity.ok()
      .contentType(MediaType.parseMediaType(file.mimeType()))
      .body(file.resource());
  }

  @GetMapping("/{uuid}/download")
  public ResponseEntity<Resource> downloadFile(@PathVariable UUID uuid) {
    FileResource file = fileService.getFile(uuid);
    ContentDisposition contentDisposition =
      ContentDisposition.attachment()
        .filename(file.originalName(), StandardCharsets.UTF_8)
        .build();
    return ResponseEntity.ok()
      .contentType(MediaType.parseMediaType(file.mimeType()))
      .header(
        HttpHeaders.CONTENT_DISPOSITION,
        contentDisposition.toString()
      )
      .body(file.resource());
  }

  @PostMapping("/upload")
  public String upload(RedirectAttributes redirectAttributes,
                       @RequestParam(name = "file") MultipartFile file) {

    if (file.isEmpty()) {
      redirectAttributes.addFlashAttribute("FailureMsg", "올리려는 파일이 비어있습니다.");
      return "redirect:/test/file";
    }
    FileDTO uploadedFileDto = fileService.toDto(fileService.upload(file, RELATIVE_PATH));
    log.info("uploadedFileDto: {}", uploadedFileDto);
    redirectAttributes.addFlashAttribute(
      "SuccessMsg",
      "파일이 성공적으로 업로드되었습니다."
    );
    return "redirect:/test/file";
  }

  @PostMapping("/delete")
  public String delete(RedirectAttributes redirectAttributes,
                       @RequestParam UUID uuid) {
    try {
      fileService.delete(uuid);
      redirectAttributes.addFlashAttribute(
        "SuccessMsg",
        "파일이 삭제되었습니다."
      );
    } catch (IllegalStateException e) {
      redirectAttributes.addFlashAttribute(
        "FailureMsg",
        "파일 삭제가 완료되지 않았습니다."
      );
    }

    return "redirect:/test/file";
  }

}
