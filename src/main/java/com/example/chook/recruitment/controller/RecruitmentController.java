package com.example.chook.recruitment.controller;

import com.example.chook.recruitment.dto.RecruitmentCreateDTO;
import com.example.chook.recruitment.dto.RecruitmentFoodTruckDTO;
import com.example.chook.recruitment.dto.RecruitmentIndividualDTO;
import com.example.chook.recruitment.dto.RecruitmentSpecificDTO;
import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import com.example.chook.recruitment.form.RecruitmentCreateForm;
import com.example.chook.recruitment.service.RecruitmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

  @GetMapping("/{id}")
  public String view(@PathVariable String id, Model model) {
    return "/recruit/detail";
  }

  @GetMapping("/register")
  public void register(Model model) {
  }

  @PostMapping("/register")
  public String register(@Valid @ModelAttribute RecruitmentCreateForm recruitmentCreateForm,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {

    if (bindingResult.hasErrors()) return "recruit/register";
    if (recruitmentCreateForm.getWorkingStartDate().isAfter(
      recruitmentCreateForm.getWorkingEndDate()))
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
      .regionSidoCode(form.getRegionSidoCode())
      .regionSigunguCode(form.getRegionSigunguCode())
      .recruitmentTitle(form.getRecruitmentTitle())
      .festivalContentId(form.getFestivalContentId())
      .content(form.getContent())
      .category(form.getCategory())
      .specific(toSpecificDto(form))
      .applicationDeadline(form.getApplicationDeadline())
      .recruitmentCount(form.getRecruitmentCount())
      .workingLocation(form.getWorkingLocation())
      .workingStartDate(form.getWorkingStartDate())
      .workingEndDate(form.getWorkingEndDate())
      .workingStartTime(form.getWorkingStartTime())
      .workingEndTime(form.getWorkingEndTime())
      .build();
  }

  private RecruitmentSpecificDTO toSpecificDto(RecruitmentCreateForm form) {
    RecruitmentCategory category = form.getCategory();
    if (category == null) return null;
    switch (category) {
      case INDIVIDUAL -> {
        return RecruitmentIndividualDTO.builder()
          .wageType(form.getWageType())
          .wageValue(form.getWageValue())
          .build();
      }
      case FOOD_TRUCK -> {
        return RecruitmentFoodTruckDTO.builder()
          .prepaid(form.isPrepaid())
          .boothFeeRequired(form.isBoothFeeRequired())
          .electricityProvided(form.isElectricityProvided())
          .build();
      }
    }
    return null;
  }

}
