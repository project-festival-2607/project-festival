package com.example.chook.admin.controller.member;

import com.example.chook.admin.condition.member.JobSeekerSearchCondition;
import com.example.chook.admin.dto.member.JobSeekerTableDTO;
import com.example.chook.admin.entity.enums.DateRangeAutofillOption;
import com.example.chook.admin.entity.enums.jobseeker.JobSeekerDateRangeType;
import com.example.chook.admin.entity.enums.jobseeker.JobSeekerKeywordType;
import com.example.chook.admin.form.member.JobSeekerSearchForm;
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
@RequestMapping("/admin/member/job-seeker")
@RequiredArgsConstructor
@Slf4j
public class AdminJobSeekerController {

  private static final int PAGINATION_SIZE = 10;
  private final AdminMemberService adminMemberService;
  private final AdminMemberInfoFieldProvider infoFieldProvider;

  @GetMapping
  public void loadJobSeekerPage(
    Model model,
    @Valid @ModelAttribute JobSeekerSearchForm form
  ) {
    model.addAttribute("form", form);
    model.addAttribute("keywordOptions", List.of(JobSeekerKeywordType.values()));
    model.addAttribute("dateRangeOptions", List.of(JobSeekerDateRangeType.values()));
    model.addAttribute("dateRangeAutofillOptions", List.of(DateRangeAutofillOption.values()));

    model.addAttribute("suspendMemberInfo", infoFieldProvider.suspend());
    model.addAttribute("unsuspendMemberInfo", infoFieldProvider.unsuspend());
    model.addAttribute("removePhoneVerificationInfo", infoFieldProvider.removePhoneVerification());
    model.addAttribute("addPhoneWithVerificationInfo", infoFieldProvider.addPhoneWithVerification());
    model.addAttribute("addBusinessRegistrationInfo", infoFieldProvider.addBusinessRegistration());
  }

  @GetMapping("/result")
  public String getJobSeekerResultFragment(
    Model model,
    @RequestParam(name = "pageIdx", required = false, defaultValue = "1") int pageIdx,
    @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
    @Valid @ModelAttribute JobSeekerSearchForm form
  ) {

    JobSeekerSearchCondition condition = JobSeekerSearchCondition.from(form);
    log.info("condition: {}", condition);
    Page<JobSeekerTableDTO> page = adminMemberService.getJobSeekerPage(pageIdx, pageSize, condition);

    model.addAttribute("page", page);
    model.addAttribute("pageSize", pageSize);
    PagingHandler<JobSeekerTableDTO, JobSeekerSearchForm> pagingHandler =
      new PagingHandler<>(page, form, PAGINATION_SIZE, pageIdx);
    model.addAttribute("pagingHandler", pagingHandler);
    model.addAttribute("pageSizeOptions", List.of(10, 30, 50));

    // thead status dropdown용
    model.addAttribute("memberStatusList", List.of(MemberStatus.values()));
    model.addAttribute("memberProviderList", List.of(Provider.values()));
    model.addAttribute("memberGenderList", List.of(Gender.values()));

    log.info("form: {}", form);
    log.info("model: {}", model);
    return "admin/member/fragments/result/job-seeker";
  }

}
