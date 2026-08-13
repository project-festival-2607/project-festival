package com.example.chook.member.service;

import com.example.chook.member.dto.*;
import com.example.chook.member.entity.enums.Provider;

import java.util.List;

public interface MemberService {

    LoginResponseDTO signUpJobSeeker(JobSeekerSignUpRequestDTO requestDTO);

    LoginResponseDTO signUpEmployer(EmployerSignUpRequestDTO requestDTO);

    LoginResponseDTO updateJobSeekerProfile(Long memberId, JobSeekerProfileUpdateRequestDTO requestDTO);

    LoginResponseDTO updateEmployerProfile(Long memberId, EmployerProfileUpdateRequestDTO requestDTO);

    LoginResponseDTO loginBySocial(Provider provider, String providerId);

    boolean hasJobSeekerAccountWithEmail(String email);

    LoginResponseDTO signUpSocial(SocialAuthSessionDTO authInfo, SocialSignUpRequestDTO requestDTO);

    boolean verifyPassword(Long memberId, String rawPassword);

    LoginResponseDTO removeBusinessNumber(Long memberId);

    void changePassword(Long memberId, String currentPassword, String newPassword, String newPasswordConfirm);

    void withdraw(Long memberId, String confirmValue);

    List<Provider> getLinkedProviders(Long memberId);

    void linkSocialAccount(Long memberId, Provider provider, String providerId);

    void unlinkSocialAccount(Long memberId, Provider provider);

    String getUsernameById(Long memberId);

}
