package com.example.chook.recruitment.controller;

import com.example.chook.recruitment.dto.*;
import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.form.RecruitmentCreateForm;
import com.example.chook.recruitment.form.RecruitmentSearchForm;
import com.example.chook.recruitment.handler.PagingHandler;
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

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/recruit/*")
@RequiredArgsConstructor
@Slf4j
public class RecruitmentController {

  private final RecruitmentService recruitmentService;

  @GetMapping("/list")
  public void list(Model model,
                   @RequestParam(name = "pageIdx", required = false, defaultValue = "1") int pageIdx,
                   @Valid @ModelAttribute RecruitmentSearchForm form,
                   BindingResult bindingResult) {
    if (form.workingStartDate().isAfter(
      form.workingEndDate()))
      bindingResult.rejectValue("workingEndDate",
        "workingEndDate.outOfRange",
        "업무시작날짜는 업무종료날짜보다 늦을 수 없습니다."
      );
    if (bindingResult.hasErrors()) return;
    RecruitmentSearchCondition condition = toCondition(form);
    Page<RecruitmentListDTO> page = recruitmentService.getPage(pageIdx, condition);
    model.addAttribute("page", page);

    PagingHandler<RecruitmentListDTO, RecruitmentSearchForm> pagingHandler =
      new PagingHandler<>(page, pageIdx, form);
    model.addAttribute("pagingHandler", pagingHandler);
  }

  @GetMapping("/manage/list")
  public void manageList(Model model) {
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
    RecruitmentCreateDTO recruitmentCreateDTO = toCreateDto(recruitmentCreateForm);
    Long recruitmentId = recruitmentService.createRecruitment(recruitmentCreateDTO);

    redirectAttributes.addAttribute("id", recruitmentId);
    redirectAttributes.addFlashAttribute("successMsg", "공고 초안이 등록되었습니다.");

    return "redirect:/recruit/{id}";
  }

  private RecruitmentCreateDTO toCreateDto(RecruitmentCreateForm form) {
    return RecruitmentCreateDTO.builder()
      .regionSidoCode(form.regionSidoCode())
      .regionSigunguCode(form.regionSigunguCode())
      .recruitmentTitle(form.recruitmentTitle())
      .festivalContentId(form.festivalContentId())
      .content(form.content())
      .category(form.category())
      .specific(toSpecificDto(form))
      .applicationDeadline(form.applicationDeadline())
      .recruitmentCount(form.recruitmentCount())
      .workingLocation(form.workingLocation())
      .workingStartDate(form.workingStartDate())
      .workingEndDate(form.workingEndDate())
      .workingStartTime(form.workingStartTime())
      .workingEndTime(form.workingEndTime())
      .build();
  }

  private RecruitmentSearchCondition toCondition(RecruitmentSearchForm form) {
    return RecruitmentSearchCondition.builder()
      .keywordList(getKeywordList(form.keywords()))
      .regionSidoCode(form.regionSidoCode())
      .regionSigunguCode(form.regionSigunguCode())
      .category(form.category())
      .status(form.status())
      .workingStartTime(form.workingStartTime())
      .workingEndTime(form.workingEndTime())
      .workingStartDate(form.workingStartDate())
      .workingEndDate(form.workingEndDate())
      .listCriteria(form.listCriteria())
      .build();
  }

  private RecruitmentSpecificDTO toSpecificDto(RecruitmentCreateForm form) {
    RecruitmentCategory category = form.category();
    if (category == null) return null;
    switch (category) {
      case INDIVIDUAL -> {
        return RecruitmentIndividualDTO.builder()
          .wageType(form.wageType())
          .wageValue(form.wageValue())
          .build();
      }
      case FOOD_TRUCK -> {
        return RecruitmentFoodTruckDTO.builder()
          .prepaid(form.prepaid())
          .boothFeeRequired(form.boothFeeRequired())
          .electricityProvided(form.electricityProvided())
          .build();
      }
    }
    return null;
  }

  private List<String> getKeywordList(String keywords) {
    return Arrays.stream(keywords.trim().split("[\\s,&]+")).toList();
  }

}
