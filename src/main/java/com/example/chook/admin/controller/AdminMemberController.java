package com.example.chook.admin.controller;

import com.example.chook.admin.record.AdminActionResponse;
import com.example.chook.admin.record.BusinessRegistrationRequest;
import com.example.chook.admin.record.PhoneVerificationRequest;
import com.example.chook.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/member")
@RequiredArgsConstructor
@Slf4j
public class AdminMemberController {

  private final AdminService adminService;

  @PostMapping("/{memberId}/suspend")
  @ResponseBody
  public AdminActionResponse suspendMember(@PathVariable Long memberId,
                                           @RequestBody(required = false) String reason) {
    log.info("target memberId: {}", memberId);
    boolean isChanged = adminService.suspendMember(memberId, reason);
    return AdminActionResponse.builder()
      .result(isChanged)
      .message(isChanged
        ? String.format("id가 %d인 사용자를 \"%s\" 사유로 정지했습니다.", memberId, reason)
        : "입력한 사유가 현재 정지 사유와 같습니다.")
      .build()
      ;
  }

  @PostMapping("/{memberId}/unsuspend")
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

  @PostMapping("/{memberId}/remove-phone-verification")
  @ResponseBody
  public AdminActionResponse removePhoneVerification(@PathVariable Long memberId) {
    adminService.removePhoneVerification(memberId);
    return AdminActionResponse.builder()
      .result(true)
      .message(String.format("id가 %d인 사용자의 휴대전화번호 인증을 삭제했으며,\n\"휴대전화번호 인증이 유효하지 않음\" 사유로 정지했습니다.", memberId))
      .build();
  }

  @PostMapping("/{memberId}/add-phone-with-verification")
  @ResponseBody
  public AdminActionResponse addPhoneWithVerification(@PathVariable Long memberId,
                                                      @RequestBody PhoneVerificationRequest request) {

    String phone = request.phone();
    String phoneVerify = request.phoneVerify();

    if (!phone.equals(phoneVerify)) {
      throw new IllegalArgumentException("입력한 휴대전화번호가 일치하지 않습니다.");
    }
    boolean isChanged = adminService.addPhoneWithVerification(memberId, phone);
    return AdminActionResponse.builder()
      .result(isChanged)
      .message(isChanged
        ? String.format("id가 %d인 사용자의 휴대전화번호가 %s로 변경되었습니다.", memberId, phone)
        : "해당 사용자의 휴대전화번호는 이미 인증되어 있습니다.")
      .build();
  }

  @PostMapping("/{memberId}/remove-business-registration")
  @ResponseBody
  public AdminActionResponse removeBusinessRegistration(@PathVariable Long memberId) {
    boolean suspended = adminService.removeBusinessRegistration(memberId);
    return AdminActionResponse.builder()
      .result(true)
      .message(String.format(
        "id가 %d인 사용자의 사업자등록번호 인증을 삭제%s했습니다.",
        memberId,
        suspended ? "했으며,\n\"사업자등록번호 인증이 유효하지 않음\" 사유로 정지" : ""))
      .build();
  }

  @PostMapping("/{memberId}/add-business-registration")
  @ResponseBody
  public AdminActionResponse addBusinessRegistration(@PathVariable Long memberId,
                                                     @RequestBody BusinessRegistrationRequest request) {

    String businessNumber = request.businessNumber();
    String businessNumberVerify = request.businessNumberVerify();

    if (!businessNumber.equals(businessNumberVerify)) {
      throw new IllegalArgumentException("입력한 사업자등록번호가 일치하지 않습니다.");
    }
    boolean isChanged = adminService.addBusinessRegistration(memberId, businessNumber);
    return AdminActionResponse.builder()
      .result(isChanged)
      .message(isChanged
        ? String.format("id가 %d인 사용자의 사업자등록번호가 \"%s\"로 변경되었습니다.", memberId, businessNumber)
        : "해당 사용자의 사업자등록번호가 이미 인증되어 있습니다.")
      .build();
  }

}
