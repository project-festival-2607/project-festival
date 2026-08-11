package com.example.chook.admin.dto;

import com.example.chook.member.entity.enums.Gender;
import com.example.chook.member.entity.enums.MemberRole;
import com.example.chook.member.entity.enums.MemberStatus;
import com.example.chook.member.entity.enums.Provider;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
  List<Provider> providers;

  Gender gender;
  LocalDate birthDate;
  String streetAddress;
  String detailAddress;

}
