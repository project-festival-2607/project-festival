package com.example.chook.admin.dto;

import java.util.List;

public interface JobSeekerTableDTOBase {

  Long getId();

  void setSocialLoginDtoList(List<SocialLoginDTO> socialLoginDtoList);
}
