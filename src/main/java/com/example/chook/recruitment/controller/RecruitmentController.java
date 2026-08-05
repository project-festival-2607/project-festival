package com.example.chook.recruitment.controller;

import com.example.chook.recruitment.form.RecruitmentCreateForm;
import com.example.chook.recruitment.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recruit/*")
@RequiredArgsConstructor
@Slf4j
public class RecruitmentController {

  private final RecruitmentService recruitmentService;

  @GetMapping("/list")
  public void list(Model model) {
  }

  @GetMapping("/manage/list")
  public void manageList(Model model) {
  }

  @GetMapping("/register")
  public void register(Model model) {

  }

  @PostMapping("/register")
  public String register(@ModelAttribute RecruitmentCreateForm recruitmentCreateForm) {
    return "redirect:/recruit/list";
  }

}
