package com.example.chook.file;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
@Slf4j
public class FileExceptionHandler {

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public String handleMaxUploadSizeExceededException(
    MaxUploadSizeExceededException e,
    RedirectAttributes redirectAttributes,
    HttpServletRequest request) {

    redirectAttributes.addFlashAttribute(
      "FailureMsg",
      "파일 최대 크기 (10MB)를 초과했습니다."
    );
    log.error("MaxUploadSizeExceededException", e);

    String referer = request.getHeader("Referer");

    if (referer == null || referer.isBlank()) {
      return "redirect:/";
    }

    return "redirect:" + referer;
  }

}