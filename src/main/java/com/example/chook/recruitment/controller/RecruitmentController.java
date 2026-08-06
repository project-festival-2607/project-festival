package com.example.chook.recruitment.controller;

import com.example.chook.recruitment.dto.RecruitmentCreateDTO;
import com.example.chook.recruitment.dto.RecruitmentListDTO;
import com.example.chook.recruitment.dto.RecruitmentManagementListDTO;
import com.example.chook.recruitment.dto.RecruitmentResponseDTO;
import com.example.chook.recruitment.form.RecruitmentCreateForm;
import com.example.chook.recruitment.form.RecruitmentManagementSearchForm;
import com.example.chook.recruitment.form.RecruitmentSearchForm;
import com.example.chook.recruitment.handler.PagingHandler;
import com.example.chook.recruitment.mapper.RecruitmentMapper;
import com.example.chook.recruitment.record.RecruitmentSearchCondition;
import com.example.chook.recruitment.service.RecruitmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/recruit/*")
@RequiredArgsConstructor
@Slf4j
public class RecruitmentController {

  private final RecruitmentService recruitmentService;
  private final RecruitmentMapper mapper;

  @GetMapping("/list")
  public void list(Model model,
                   @RequestParam(name = "pageIdx", required = false, defaultValue = "1") int pageIdx,
                   @Valid @ModelAttribute RecruitmentSearchForm form,
                   BindingResult bindingResult) {
    if (bindingResult.hasErrors()) return;
    if (form.workingStartDate().isAfter(
      form.workingEndDate()))
      bindingResult.rejectValue("workingEndDate",
        "workingEndDate.outOfRange",
        "업무시작날짜는 업무종료날짜보다 늦을 수 없습니다."
      );
    if (bindingResult.hasErrors()) return;
    RecruitmentSearchCondition condition = mapper.toCondition(form);
    Page<RecruitmentListDTO> page = recruitmentService.getPage(pageIdx, condition);
    model.addAttribute("page", page);

    PagingHandler<RecruitmentListDTO, RecruitmentSearchForm> pagingHandler =
      new PagingHandler<>(page, pageIdx, form);
    model.addAttribute("pagingHandler", pagingHandler);
  }

  @GetMapping("/manage/list")
  public void manageList(Model model,
                         @RequestParam(name = "pageIdx", required = false, defaultValue = "1") int pageIdx,
                         @Valid @ModelAttribute RecruitmentManagementSearchForm form,
                         BindingResult bindingResult) {
    if (bindingResult.hasErrors()) return;
    RecruitmentSearchCondition condition = mapper.toCondition(form);
    Page<RecruitmentManagementListDTO> page = recruitmentService.getManagementPage(pageIdx, condition);
    model.addAttribute("page", page);

    PagingHandler<RecruitmentManagementListDTO, RecruitmentManagementSearchForm> pagingHandler =
      new PagingHandler<>(page, pageIdx, form);
    model.addAttribute("pagingHandler", pagingHandler);

  }

  @GetMapping("/{id}")
  public String view(@PathVariable Long id, Model model) {
    RecruitmentResponseDTO responseDto = recruitmentService.getRecruitment(id);
    model.addAttribute("recruitment", responseDto);
    return "recruit/detail";
  }

  @GetMapping("/register")
  public void register(Model model) {
  }

  @PostMapping("/register")
  public String register(@Valid @ModelAttribute RecruitmentCreateForm recruitmentCreateForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) return "recruit/register";
    if (recruitmentCreateForm.workingStartDate().isAfter(
      recruitmentCreateForm.workingEndDate()))
      bindingResult.rejectValue("workingEndDate",
        "workingEndDate.outOfRange",
        "업무시작날짜는 업무종료날짜보다 늦을 수 없습니다."
      );
    if (bindingResult.hasErrors()) return "recruit/register";
    RecruitmentCreateDTO recruitmentCreateDTO = mapper.toCreateDto(recruitmentCreateForm);
    Long recruitmentId = recruitmentService.createRecruitment(recruitmentCreateDTO);

    redirectAttributes.addAttribute("id", recruitmentId);
    redirectAttributes.addFlashAttribute("successMsg", "공고 초안이 등록되었습니다.");

    return "redirect:/recruit/{id}";
  }





}
