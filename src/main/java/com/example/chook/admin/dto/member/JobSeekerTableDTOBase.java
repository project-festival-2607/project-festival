package com.example.chook.admin.dto.member;

import java.util.List;

public interface JobSeekerTableDTOBase {

  Long getId();

  void setSocialLoginDtoList(List<SocialLoginDTO> socialLoginDtoList);
}
