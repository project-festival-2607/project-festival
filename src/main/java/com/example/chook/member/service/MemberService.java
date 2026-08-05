package com.example.chook.member.service;

import com.example.chook.member.dto.LoginRequestDTO;
import com.example.chook.member.dto.LoginResponseDTO;

public interface MemberService {

    LoginResponseDTO login(LoginRequestDTO requestDTO);
}
