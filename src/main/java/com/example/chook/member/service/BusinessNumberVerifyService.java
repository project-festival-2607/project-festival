package com.example.chook.member.service;

import com.example.chook.member.dto.BusinessNumberVerifyRequestDTO;
import com.example.chook.member.dto.BusinessNumberVerifyResponseDTO;

public interface BusinessNumberVerifyService {

    BusinessNumberVerifyResponseDTO verify(BusinessNumberVerifyRequestDTO requestDTO);

}
