package com.example.chook.admin.dto.member;

import com.example.chook.common.util.CustomStringUtils;
import com.example.chook.member.entity.enums.Gender;
import com.example.chook.member.entity.enums.MemberRole;
import com.example.chook.member.entity.enums.MemberStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobSeekerTableDTO implements JobSeekerTableDTOBase {

  // Member 공통 필드
  Long id;
  String username;
  String name;
  String phone;
  Boolean phoneVerified;
  String email;
  MemberRole role;
  MemberStatus status;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
  LocalDateTime lastLoginAt;
  LocalDateTime deletedAt;
  Long point;
  LocalDateTime suspendedAt;
  String suspendedReason;

  // JOB_SEEKER 전용 필드
  @Builder.Default
  List<SocialLoginDTO> socialLoginDtoList = new ArrayList<>();
  Gender gender;
  LocalDate birthDate;
  String streetAddress;
  String detailAddress;

  public String getFormattedPhone() {
    return CustomStringUtils.getFormattedPhone(phone);
  }

}
