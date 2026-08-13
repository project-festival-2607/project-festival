package com.example.chook.admin.controller;

import com.example.chook.admin.dto.JobSeekerTableDTO;
import com.example.chook.admin.form.JobSeekerSearchForm;
import com.example.chook.admin.record.AdminActionResponse;
import com.example.chook.admin.record.JobSeekerSearchCondition;
import com.example.chook.admin.service.AdminService;
import com.example.chook.common.handler.PagingHandler;
import com.example.chook.member.entity.enums.MemberStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

  private static final int PAGINATION_SIZE = 10;
  private final AdminService adminService;

  @GetMapping("/member/job-seeker")
  public void jobSeekerList(Model model,
                            @RequestParam(name = "pageIdx", required = false, defaultValue = "1") int pageIdx,
                            @RequestParam(name = "pageSize", required = false, defaultValue = "30") int pageSize,
                            @Valid @ModelAttribute JobSeekerSearchForm form,
                            BindingResult bindingResult) {

    if (bindingResult.hasErrors()) return;
    JobSeekerSearchCondition condition = new JobSeekerSearchCondition(form);
    Page<JobSeekerTableDTO> page = adminService.getPage(pageIdx, pageSize, condition);

    model.addAttribute("page", page);
    model.addAttribute("pageSize", pageSize);
    PagingHandler<JobSeekerTableDTO, JobSeekerSearchForm> pagingHandler =
      new PagingHandler<>(page, form, PAGINATION_SIZE, pageIdx);
    model.addAttribute("pagingHandler", pagingHandler);

    model.addAttribute("memberStatusFilters", List.of(
      MemberStatus.ACTIVE,
      MemberStatus.DORMANT,
      MemberStatus.SUSPENDED
    ));

  }

  @PostMapping("/member/job-seeker/remove-phone-verification")
  @ResponseBody
  public AdminActionResponse removePhoneVerification(@RequestParam Long memberId) {
    adminService.removePhoneVerification(memberId);
    return AdminActionResponse.builder()
      .result(true)
      .message(String.format("id가 %d인 사용자의 전화번호 인증 삭제 및 정지[SUSPENDED] 상태로의 전환을 완료했습니다.", memberId))
      .build();
  }

  @PostMapping("/member/job-seeker/add-phone-with-verification")
  @ResponseBody
  public AdminActionResponse addPhoneWithVerification(@RequestParam Long memberId,
                                                      @RequestParam String phone) {
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
                                           @RequestBody String reason) {
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

  @PostMapping("/member/job-seeker/unsuspend-member")
  public AdminActionResponse unsuspendMember(@RequestParam Long memberId) {
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
