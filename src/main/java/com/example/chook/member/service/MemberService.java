package com.example.chook.member.service;

import com.example.chook.member.dto.*;

public interface MemberService {

    LoginResponseDTO login(LoginRequestDTO requestDTO);

    LoginResponseDTO signUpJobSeeker(JobSeekerSignUpRequestDTO requestDTO);

    LoginResponseDTO signUpEmployer(EmployerSignUpRequestDTO requestDTO);

    LoginResponseDTO updateProfile(Long memberId, JobSeekerProfileUpdateRequestDTO requestDTO);
}
