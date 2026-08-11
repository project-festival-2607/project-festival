package com.example.chook.admin.controller;

import com.example.chook.admin.form.JobSeekerSearchForm;
import com.example.chook.admin.record.JobSeekerSearchCondition;
import com.example.chook.admin.service.AdminService;
import com.example.chook.common.handler.PagingHandler;
import com.example.chook.admin.dto.JobSeekerTableDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

  private final AdminService adminService;

  private static final int PAGINATION_SIZE = 10;

  @GetMapping("/member/job-seeker")
  public void jobSeekerList(Model model,
                            @RequestParam(name = "pageIdx", required = false, defaultValue = "1") int pageIdx,
                            @Valid @ModelAttribute JobSeekerSearchForm form,
                            BindingResult bindingResult) {

    if (bindingResult.hasErrors()) return;
    JobSeekerSearchCondition condition =  new JobSeekerSearchCondition(form);
    Page<JobSeekerTableDTO> page = adminService.getPage(pageIdx, condition);

    model.addAttribute("page", page);
    PagingHandler<JobSeekerTableDTO, JobSeekerSearchForm> pagingHandler =
      new PagingHandler<>(page, form, PAGINATION_SIZE, pageIdx);
    model.addAttribute("pagingHandler", pagingHandler);

  }

}
