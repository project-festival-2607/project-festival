package com.example.chook.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/file")
@RequiredArgsConstructor
@Slf4j
public class FileController {

  private final FileService fileService;

  @PostMapping("/upload")
  public String upload(RedirectAttributes redirectAttributes,
                       @RequestParam(name = "file") MultipartFile file) {

    if (file.isEmpty()) {
      redirectAttributes.addFlashAttribute("uploadFailMsg", "올리려는 파일이 비어있습니다.");
      return "redirect:/test/file";
    }
    FileDTO uploadedFileDto = fileService.uploadAndGetDto(file);
    log.info("uploadedFileDto: {}", uploadedFileDto);
    redirectAttributes.addFlashAttribute(
      "uploadSuccessMsg",
      "파일이 성공적으로 업로드되었습니다."
    );
    return "redirect:/test/file";
  }

  @PostMapping("/delete")
  public String delete(RedirectAttributes redirectAttributes,
                       @RequestParam String uuid) {
    fileService.delete(uuid);
    redirectAttributes.addFlashAttribute(
      "deleteSuccessMsg",
      "파일이 삭제되었습니다."
    );
    return "redirect:/test/file";
  }

}
