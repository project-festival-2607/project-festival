package com.example.chook.admin.mapper;

import com.example.chook.admin.dto.member.RecruiterTableDTO;
import com.example.chook.admin.dto.member.SocialLoginDTO;
import com.example.chook.member.entity.BusinessRegistration;
import com.example.chook.member.entity.EmployerProfile;
import com.example.chook.member.entity.Member;
import com.example.chook.member.entity.SocialLogin;
import org.springframework.stereotype.Component;

@Component
public class AdminMapper {

  public SocialLoginDTO toDto(SocialLogin socialLogin) {
    return SocialLoginDTO.builder()
      .provider(socialLogin.getProvider())
      .linkedAt(socialLogin.getLinkedAt())
      .build();
  }

  public RecruiterTableDTO toDto(Member member, EmployerProfile employerProfile, BusinessRegistration businessRegistration) {
    return RecruiterTableDTO.builder()
      .id(member.getId())
      .username(member.getUsername())
      .name(member.getName())
      .phone(member.getPhone())
      .phoneVerified(member.isPhoneVerified())
      .email(member.getEmail())
      .role(member.getRole())
      .status(member.getStatus())
      .createdAt(member.getCreatedAt())
      .updatedAt(member.getUpdatedAt())
      .lastLoginAt(member.getLastLoginAt())
      .deletedAt(member.getDeletedAt())
      .point(member.getPoint())
      // SUSPENDED가 아님을 보장하므로 생략
      .companyName(employerProfile.getCompanyName())
      .ceoName(employerProfile.getCeoName())
      .foundedAt(employerProfile.getFoundedAt())
      .streetAddress(employerProfile.getStreetAddress())
      .detailAddress(employerProfile.getDetailAddress())
      .businessNumber(businessRegistration.getBusinessNumber())
      .businessNumberVerifiedAt(businessRegistration.getVerifiedAt())
      .build();
  }
}
