package com.example.chook.file.controller;

import com.example.chook.file.dto.FileDTO;
import com.example.chook.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/test/file")
@RequiredArgsConstructor
@Slf4j
public class TestFileController {

  private final FileService fileService;
  private static final String RELATIVE_PATH = "test";

  @GetMapping
  public void file(Model model) {

    List<FileDTO> fileDtoList = fileService.getList();
    log.info("fileDtoList: {}", fileDtoList);
    model.addAttribute("fileList", fileDtoList);

  }

  @PostMapping("/upload")
  public String upload(RedirectAttributes redirectAttributes,
                       @RequestParam(name = "file") MultipartFile file) {

    if (file.isEmpty()) {
      redirectAttributes.addFlashAttribute("FailureMsg", "올리려는 파일이 비어있습니다.");
      return "redirect:/test/file";
    }
    FileDTO uploadedFileDto = fileService.uploadAndGetDto(file, RELATIVE_PATH);
    log.info("uploadedFileDto: {}", uploadedFileDto);
    redirectAttributes.addFlashAttribute(
      "SuccessMsg",
      "파일이 성공적으로 업로드되었습니다."
    );
    return "redirect:/test/file";
  }

  @PostMapping("/delete")
  public String delete(RedirectAttributes redirectAttributes,
                       @RequestParam String uuid) {
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
