package com.example.chook.admin.dto;

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
public class JobSeekerTableDTO {

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
  List<SocialLoginDTO> socialLoginDtoList = new ArrayList<>();

  Gender gender;
  LocalDate birthDate;
  String streetAddress;
  String detailAddress;

  LocalDateTime suspendedAt;
  String suspendedReason;

  public String getFormattedPhone() {
    return phone.length() == 11
      ? String.format("%s-%s-%s", phone.substring(0, 3), phone.substring(3, 7), phone.substring(7))
      : phone;
  }
}
