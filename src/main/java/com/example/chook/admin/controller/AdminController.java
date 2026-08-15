package com.example.chook.admin.controller;

import com.example.chook.admin.dto.JobSeekerTableDTO;
import com.example.chook.admin.entity.enums.JobSeekerKeywordType;
import com.example.chook.admin.form.JobSeekerSearchForm;
import com.example.chook.admin.mapper.AdminMapper;
import com.example.chook.admin.record.AdminActionResponse;
import com.example.chook.admin.record.DropdownOption;
import com.example.chook.admin.record.JobSeekerSearchCondition;
import com.example.chook.admin.record.PhoneVerificationRequest;
import com.example.chook.admin.service.AdminService;
import com.example.chook.common.handler.PagingHandler;
import com.example.chook.member.entity.enums.MemberStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

  private static final int PAGINATION_SIZE = 10;
  private final AdminService adminService;
  private final AdminMapper adminMapper;

  @GetMapping("/member/job-seeker")
  public void loadJobSeekerPage(
    Model model,
    @RequestParam(name = "pageSize", required = false, defaultValue = "30") int pageSize,
    @Valid @ModelAttribute JobSeekerSearchForm form
  ) {
    model.addAttribute("pageSize", pageSize);
    model.addAttribute("form", form);

    List<DropdownOption> keywordOptions = Stream.concat(
      Stream.of(new DropdownOption("ALL", "전체", false)),
      Arrays.stream(JobSeekerKeywordType.values())
        .map(adminMapper::toDropdownOption)
    ).toList();

    model.addAttribute("keywordOptions",  keywordOptions);
  }

  @GetMapping("/member/job-seeker/result")
  public String getJobSeekerResultFragment(
    Model model,
    @RequestParam(name = "pageIdx", required = false, defaultValue = "1") int pageIdx,
    @RequestParam(name = "pageSize", required = false, defaultValue = "30") int pageSize,
    @Valid @ModelAttribute JobSeekerSearchForm form
  ) {

    JobSeekerSearchCondition condition = new JobSeekerSearchCondition(form);
    log.info("condition: {}", condition);
    Page<JobSeekerTableDTO> page = adminService.getPage(pageIdx, pageSize, condition);

    model.addAttribute("page", page);
    PagingHandler<JobSeekerTableDTO, JobSeekerSearchForm> pagingHandler =
      new PagingHandler<>(page, form, PAGINATION_SIZE, pageIdx);
    model.addAttribute("pagingHandler", pagingHandler);

    // thead status dropdown용
    model.addAttribute("memberStatusFilters", List.of(
      MemberStatus.ACTIVE,
      MemberStatus.DORMANT,
      MemberStatus.SUSPENDED
    ));

    log.info("form: {}", form);
    log.info("model: {}", model);
    return "admin/fragments/job-seeker-result";
  }

  @PostMapping("/member/job-seeker/{memberId}/remove-phone-verification")
  @ResponseBody
  public AdminActionResponse removePhoneVerification(@PathVariable Long memberId) {
    adminService.removePhoneVerification(memberId);
    return AdminActionResponse.builder()
      .result(true)
      .message(String.format("id가 %d인 사용자의 전화번호 인증을 삭제했으며,\n\"휴대전화 인증 해제\" 사유로 정지했습니다.", memberId))
      .build();
  }

  @PostMapping("/member/job-seeker/{memberId}/add-phone-with-verification")
  @ResponseBody
  public AdminActionResponse addPhoneWithVerification(@PathVariable Long memberId,
                                                      @RequestBody PhoneVerificationRequest request) {

    String phone = request.phone();
    String phoneVerify = request.phoneVerify();

    if (!phone.equals(phoneVerify)) {
      throw new IllegalArgumentException("입력한 휴대전화 번호가 일치하지 않습니다.");
    }
    boolean isChanged = adminService.addPhoneWithVerification(memberId, phone);
    return AdminActionResponse.builder()
      .result(isChanged)
      .message(isChanged
        ? String.format("id가 %d인 사용자의 전화번호가 %s로 변경되었습니다.", memberId, phone)
        : "해당 사용자는 이미 전화번호가 인증된 상태입니다.")
      .build();
  }

  @PostMapping("/member/job-seeker/{memberId}/suspend")
  @ResponseBody
  public AdminActionResponse suspendMember(@PathVariable Long memberId,
                                           @RequestBody(required = false) String reason) {
    log.info("target memberId: {}", memberId);
    boolean isChanged = adminService.suspendMember(memberId, reason);
    return AdminActionResponse.builder()
      .result(isChanged)
      .message(isChanged
        ? String.format("id가 %d인 사용자를 %s 사유로 정지했습니다.", memberId, reason)
        : "입력한 사유가 현재 정지 사유와 같습니다.")
      .build()
      ;

  }

  @PostMapping("/member/job-seeker/{memberId}/unsuspend")
  @ResponseBody
  public AdminActionResponse unsuspendMember(@PathVariable Long memberId) {
    boolean isChanged = adminService.unsuspendMember(memberId);
    return AdminActionResponse.builder()
      .result(isChanged)
      .message(isChanged
        ? String.format("id가 %d인 사용자의 정지를 해제했습니다.", memberId)
        : "해당 사용자는 정지되어 있지 않습니다.")
      .build()
      ;
  }

}
