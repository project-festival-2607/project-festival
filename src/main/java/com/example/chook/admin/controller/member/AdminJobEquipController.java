package com.example.chook.admin.controller.member;

import com.example.chook.admin.condition.member.JobEquipSearchCondition;
import com.example.chook.admin.dto.JobEquipTableDTO;
import com.example.chook.admin.entity.enums.DateRangeAutofillOption;
import com.example.chook.admin.entity.enums.jobequip.JobEquipDateRangeType;
import com.example.chook.admin.entity.enums.jobequip.JobEquipKeywordType;
import com.example.chook.admin.form.member.JobSeekerSearchForm;
import com.example.chook.admin.form.member.RecruiterSearchForm;
import com.example.chook.admin.provider.AdminMemberInfoFieldProvider;
import com.example.chook.admin.service.AdminMemberService;
import com.example.chook.common.handler.PagingHandler;
import com.example.chook.member.entity.enums.Gender;
import com.example.chook.member.entity.enums.MemberStatus;
import com.example.chook.member.entity.enums.Provider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/member/job-equip")
@RequiredArgsConstructor
@Slf4j
public class AdminJobEquipController {

  private static final int PAGINATION_SIZE = 10;
  private final AdminMemberService adminMemberService;
  private final AdminMemberInfoFieldProvider infoFieldProvider;

  @GetMapping
  public void loadJobEquipPage(
    Model model,
    @Valid @ModelAttribute RecruiterSearchForm form
  ) {
    model.addAttribute("form", form);
    model.addAttribute("keywordOptions", List.of(JobEquipKeywordType.values()));
    model.addAttribute("dateRangeOptions", List.of(JobEquipDateRangeType.values()));
    model.addAttribute("dateRangeAutofillOptions", List.of(DateRangeAutofillOption.values()));

    model.addAttribute("suspendMemberInfo", infoFieldProvider.suspend());
    model.addAttribute("unsuspendMemberInfo", infoFieldProvider.unsuspend());
    model.addAttribute("removePhoneVerificationInfo", infoFieldProvider.removePhoneVerification());
    model.addAttribute("addPhoneWithVerificationInfo", infoFieldProvider.addPhoneWithVerification());
    model.addAttribute("removeBusinessRegistrationInfo", infoFieldProvider.removeBusinessRegistration());
  }

  @GetMapping("/result")
  public String getJobEquipResultFragment(
    Model model,
    @RequestParam(name = "pageIdx", required = false, defaultValue = "1") int pageIdx,
    @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
    @Valid @ModelAttribute JobSeekerSearchForm form
  ) {

    JobEquipSearchCondition condition = JobEquipSearchCondition.from(form);
    log.info("condition: {}", condition);
    Page<JobEquipTableDTO> page = adminMemberService.getJobEquipPage(pageIdx, pageSize, condition);

    model.addAttribute("page", page);
    model.addAttribute("pageSize", pageSize);
    PagingHandler<JobEquipTableDTO, JobSeekerSearchForm> pagingHandler =
      new PagingHandler<>(page, form, PAGINATION_SIZE, pageIdx);
    model.addAttribute("pagingHandler", pagingHandler);
    model.addAttribute("pageSizeOptions", List.of(10, 30, 50));

    // thead status dropdown용
    model.addAttribute("memberStatusList", List.of(MemberStatus.values()));
    model.addAttribute("memberProviderList", List.of(Provider.values()));
    model.addAttribute("memberGenderList", List.of(Gender.values()));

    log.info("form: {}", form);
    log.info("model: {}", model);
    return "admin/member/fragments/result/job-equip";
  }

}
