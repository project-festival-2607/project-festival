package com.example.chook.member.service;

import com.example.chook.member.dto.*;
import com.example.chook.member.entity.enums.Provider;

public interface MemberService {

    LoginResponseDTO signUpJobSeeker(JobSeekerSignUpRequestDTO requestDTO);

    LoginResponseDTO signUpEmployer(EmployerSignUpRequestDTO requestDTO);

    LoginResponseDTO updateProfile(Long memberId, JobSeekerProfileUpdateRequestDTO requestDTO);

    LoginResponseDTO loginBySocial(Provider provider, String providerId);

    boolean hasJobSeekerAccountWithEmail(String email);

    LoginResponseDTO signUpSocial(SocialAuthSessionDTO authInfo, SocialSignUpRequestDTO requestDTO);
}
