package com.example.chook.admin.controller.member;

import com.example.chook.admin.condition.RecruiterSearchCondition;
import com.example.chook.admin.dto.RecruiterTableDTO;
import com.example.chook.admin.entity.enums.DateRangeAutofillOption;
import com.example.chook.admin.entity.enums.recruiter.RecruiterDateRangeType;
import com.example.chook.admin.entity.enums.recruiter.RecruiterKeywordType;
import com.example.chook.admin.form.RecruiterSearchForm;
import com.example.chook.admin.provider.AdminMemberInfoFieldProvider;
import com.example.chook.admin.service.AdminMemberService;
import com.example.chook.common.handler.PagingHandler;
import com.example.chook.member.entity.enums.MemberStatus;
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
@RequestMapping("/admin/member/recruiter")
@RequiredArgsConstructor
@Slf4j
public class AdminRecruiterController {

  private static final int PAGINATION_SIZE = 10;
  private final AdminMemberService adminMemberService;
  private final AdminMemberInfoFieldProvider infoFieldProvider;

  @GetMapping
  public void loadRecruiterPage(
    Model model,
    @Valid @ModelAttribute RecruiterSearchForm form
  ) {
    model.addAttribute("form", form);
    model.addAttribute("keywordOptions", List.of(RecruiterKeywordType.values()));
    model.addAttribute("dateRangeOptions", List.of(RecruiterDateRangeType.values()));
    model.addAttribute("dateRangeAutofillOptions", List.of(DateRangeAutofillOption.values()));

    model.addAttribute("suspendMemberInfo", infoFieldProvider.suspend());
    model.addAttribute("unsuspendMemberInfo", infoFieldProvider.unsuspend());
    model.addAttribute("removePhoneVerificationInfo", infoFieldProvider.removePhoneVerification());
    model.addAttribute("addPhoneWithVerificationInfo", infoFieldProvider.addPhoneWithVerification());
    model.addAttribute("removeBusinessRegistrationInfo", infoFieldProvider.removeBusinessRegistrationWithCompanyInfo());
    model.addAttribute("addBusinessRegistrationInfo", infoFieldProvider.addBusinessRegistrationWithCompanyInfo());
  }

  @GetMapping("/result")
  public String getRecruiterResultFragment(
    Model model,
    @RequestParam(name = "pageIdx", required = false, defaultValue = "1") int pageIdx,
    @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
    @Valid @ModelAttribute RecruiterSearchForm form
  ) {

    RecruiterSearchCondition condition = RecruiterSearchCondition.from(form);
    log.info("condition: {}", condition);
    Page<RecruiterTableDTO> page = adminMemberService.getRecruiterPage(pageIdx, pageSize, condition);

    model.addAttribute("page", page);
    model.addAttribute("pageSize", pageSize);
    PagingHandler<RecruiterTableDTO, RecruiterSearchForm> pagingHandler =
      new PagingHandler<>(page, form, PAGINATION_SIZE, pageIdx);
    model.addAttribute("pagingHandler", pagingHandler);
    model.addAttribute("pageSizeOptions", List.of(10, 30, 50));

    // thead status dropdown용
    model.addAttribute("memberStatusList", List.of(MemberStatus.values()));

    log.info("form: {}", form);
    log.info("model: {}", model);
    return "admin/member/fragments/result/recruiter";
  }

}
