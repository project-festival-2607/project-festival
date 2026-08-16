package com.example.chook.admin.controller;

import com.example.chook.admin.condition.JobSeekerSearchCondition;
import com.example.chook.admin.dto.JobSeekerTableDTO;
import com.example.chook.admin.entity.enums.DateRangeAutofillOption;
import com.example.chook.admin.entity.enums.jobseeker.JobSeekerDateCriteria;
import com.example.chook.admin.entity.enums.jobseeker.JobSeekerKeywordType;
import com.example.chook.admin.form.JobSeekerSearchForm;
import com.example.chook.admin.record.MemberInfoField;
import com.example.chook.admin.service.AdminService;
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
  private final AdminService adminService;

  @GetMapping
  public void loadJobSeekerPage(
    Model model,
    @Valid @ModelAttribute JobSeekerSearchForm form
  ) {
    model.addAttribute("form", form);
    model.addAttribute("keywordOptions", List.of(JobSeekerKeywordType.values()));
    model.addAttribute("dateRangeOptions", List.of(JobSeekerDateCriteria.values()));
    model.addAttribute("dateRangeAutofillOptions", List.of(DateRangeAutofillOption.values()));

    model.addAttribute("suspendMemberInfo", List.of(
      new MemberInfoField("아이디", "username"),
      new MemberInfoField("이름", "name"),
      new MemberInfoField("이메일", "email"),
      new MemberInfoField("휴대전화번호", "phone"),
      new MemberInfoField("최근접속일시", "lastLoginAt")
    ));

    model.addAttribute("unsuspendMemberInfo", List.of(
      new MemberInfoField("아이디", "username"),
      new MemberInfoField("이름", "name"),
      new MemberInfoField("정지일시", "suspendedAt"),
      new MemberInfoField("정지사유", "suspendedReason")
    ));

    model.addAttribute("removePhoneVerificationInfo", List.of(
      new MemberInfoField("아이디", "username"),
      new MemberInfoField("이름", "name"),
      new MemberInfoField("휴대전화번호", "phone")
    ));

    model.addAttribute("addPhoneWithVerificationInfo", List.of(
      new MemberInfoField("아이디", "username"),
      new MemberInfoField("이름", "name"),
      new MemberInfoField("기존 휴대전화번호", "phone")
    ));
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
    Page<JobSeekerTableDTO> page = adminService.getPage(pageIdx, pageSize, condition);

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
