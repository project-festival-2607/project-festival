package com.example.chook.admin.mapper;

import com.example.chook.admin.dto.SocialLoginDTO;
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

}
