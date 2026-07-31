package com.example.chook.test;

import com.example.chook.file.FileDTO;
import com.example.chook.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

  private final FileService fileService;

  @GetMapping("/file")
  public void file(Model model) {

    List<FileDTO> fileDtoList = fileService.getList();
    log.info("fileDtoList: {}", fileDtoList);
    model.addAttribute("fileList", fileDtoList);

  }

}