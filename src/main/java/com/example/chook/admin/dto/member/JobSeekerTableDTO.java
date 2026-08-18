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
  private Long id;
  private String username;
  private String name;
  private String phone;
  private Boolean phoneVerified;
  private String email;
  private MemberRole role;
  private MemberStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime lastLoginAt;
  private LocalDateTime deletedAt;
  private Long point;
  private LocalDateTime suspendedAt;
  private String suspendedReason;

  // JOB_SEEKER 전용 필드
  @Builder.Default
  private List<SocialLoginDTO> socialLoginDtoList = new ArrayList<>();
  private Gender gender;
  private LocalDate birthDate;
  private String streetAddress;
  private String detailAddress;

  public String getFormattedPhone() {
    return CustomStringUtils.getFormattedPhone(phone);
  }

}
