package com.example.chook.member.service;

import com.example.chook.member.BusinessNumberTokenProvider;
import com.example.chook.member.dto.*;
import com.example.chook.member.entity.*;
import com.example.chook.member.entity.enums.MemberRole;
import com.example.chook.member.entity.enums.MemberStatus;
import com.example.chook.member.entity.enums.Provider;
import com.example.chook.member.exception.MemberDormantException;
import com.example.chook.member.exception.MemberSuspendedException;
import com.example.chook.member.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository; // 회원 기본정보 DB 저장/조회
    private final JobSeekerProfileRepository jobSeekerProfileRepository; // 구직자 프로필 DB 저장
    private final EmployerProfileRepository employerProfileRepository; // 구인자 프로필 DB 저장
    private final BusinessRegistrationRepository businessRegistrationRepository; // 사업자등록정보 DB 저장
    private final BusinessNumberTokenProvider businessNumberTokenProvider; // 사업자번호 인증 토큰 발급 및 검증
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화 및 입력값 해시 일치 여부 확인
    private final SocialLoginRepository socialLoginRepository; //
    private static final List<MemberRole> JOB_SEEKER_ROLES = List.of(MemberRole.JOB_SEEKER, MemberRole.JOB_EQUIP);

    @Transactional
    @Override
    // 구직자 회원 가입
    public LoginResponseDTO signUpJobSeeker(JobSeekerSignUpRequestDTO requestDTO) {

        // 아이디 중복 확인
        validateUsernameNotTaken(requestDTO.getUserName());

        // 사업자번호가 입력되어 있을 때 인증 토큰 유효 확인
        // true → 사업자번호 인증 완료
        // false → 사업자번호 자체를 입력하지 않음
        // IllegalArgumentException → 사업자번호를 입력했으나 인증 확인에 실패함
        boolean isJobEquip = hasVerifiedBusinessNumber(
                requestDTO.getBusinessNumber(),
                requestDTO.getVerificationToken()
        );

        // 공통 회원 정보 Member Entity를 DB에 저장
        // 사업자번호 인증됨 → JOB_EQUIP
        // 사업자번호 인증 안 됨 → JOB_SEEKER
        Member member = memberRepository.save(buildMember(
                requestDTO.getUserName(),
                requestDTO.getPassword(),
                requestDTO.getName(),
                requestDTO.getPhone(),
                requestDTO.getEmail(),
                isJobEquip ? MemberRole.JOB_EQUIP : MemberRole.JOB_SEEKER
        ));

        // 구직자 전용 정보 저장
        jobSeekerProfileRepository.save(JobSeekerProfile.builder()
                .member(member)
                .gender(requestDTO.getGender())
                .birthDate(requestDTO.getBirthDate())
                .streetAddress(requestDTO.getStreetAddress())
                .detailAddress(requestDTO.getDetailAddress())
                .build());

        // 사업자번호를 인증한 JOB_EQUIP이라면
        // 사업자등록 정보를 별도 테이블에 저장
        if (isJobEquip) {
            saveBusinessRegistration(member, requestDTO.getBusinessNumber());
        }

        // 가입 완료 후 생성된 회원 정보를 로그인 응답 형태로 반환
        return toLoginResponseDTO(member);
    }

    @Transactional
    @Override
    // 구인자 회원가입
    public LoginResponseDTO signUpEmployer(EmployerSignUpRequestDTO requestDTO) {

        // 아이디 중복 확인
        validateUsernameNotTaken(requestDTO.getUsername());

        // 구인자는 사업자 인증 필수
        // verify()에서 입력받은 사업자번호와 토큰 정보가 일치하는지 확인
        if (!businessNumberTokenProvider.verify(requestDTO.getBusinessNumber(), requestDTO.getVerificationToken())) {
            // 인증되지 않은 사업자번호라면 회원가입을 중단
            throw new IllegalArgumentException("사업자번호 인증이 유효하지 않습니다.");
        }

        // 공통 회원 정보 저장
        // 구인자의 ROLE은 항상 RECRUITER
        Member member = memberRepository.save(buildMember(
                requestDTO.getUsername(),
                requestDTO.getPassword(),
                requestDTO.getName(),
                requestDTO.getPhone(),
                requestDTO.getEmail(),
                MemberRole.RECRUITER
        ));

        // 구인자 전용 정보 저장
        employerProfileRepository.save(EmployerProfile.builder()
                .member(member)
                .companyName(requestDTO.getCompanyName())
                .ceoName(requestDTO.getCeoName())
                .streetAddress(requestDTO.getStreetAddress())
                .detailAddress(requestDTO.getDetailAddress())
                .foundedAt(requestDTO.getFoundedAt())
                .build());

        // 인증된 사업자등록번호 저장
        saveBusinessRegistration(member, requestDTO.getBusinessNumber());

        // 가입 완료 후 생성된 회원 정보를 로그인 응답 형태로 반환
        return toLoginResponseDTO(member);
    }

    @Transactional
    @Override
    // 프로필 수정
    public LoginResponseDTO updateJobSeekerProfile(Long memberId, JobSeekerProfileUpdateRequestDTO requestDTO) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        JobSeekerProfile profile = jobSeekerProfileRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("프로필을 찾을 수 없습니다."));

        member.setEmail(requestDTO.getEmail());
        // phone/phoneVerified 갱신은 전화번호 인증 서비스 구현 후 추가

        profile.setStreetAddress(requestDTO.getStreetAddress());
        profile.setDetailAddress(requestDTO.getDetailAddress());

        if (requestDTO.getBusinessNumber() != null && !requestDTO.getBusinessNumber().isBlank()) {
            registerOrUpdateBusinessNumber(member, requestDTO.getBusinessNumber(), requestDTO.getVerificationToken());
        }

        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);

        return toLoginResponseDTO(member);
    }

    @Transactional
    @Override
    public LoginResponseDTO updateEmployerProfile(Long memberId, EmployerProfileUpdateRequestDTO requestDTO) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        EmployerProfile profile = employerProfileRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("프로필을 찾을 수 없습니다."));

        BusinessRegistration registration = businessRegistrationRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사업자등록 정보를 찾을 수 없습니다."));

        // 제출된 사업자번호가 기존과 다르면 = 변경 시도 → 재인증 필수
        if (!registration.getBusinessNumber().equals(requestDTO.getBusinessNumber())) {
            if (!businessNumberTokenProvider.verify(requestDTO.getBusinessNumber(), requestDTO.getVerificationToken())) {
                throw new IllegalArgumentException("사업자번호 인증이 유효하지 않습니다.");
            }
            registration.setBusinessNumber(requestDTO.getBusinessNumber());
            registration.setVerifiedAt(LocalDateTime.now());
            businessRegistrationRepository.save(registration);
        }

        member.setEmail(requestDTO.getEmail());
        profile.setCompanyName(requestDTO.getCompanyName());
        profile.setCeoName(requestDTO.getCeoName());
        profile.setStreetAddress(requestDTO.getStreetAddress());
        profile.setDetailAddress(requestDTO.getDetailAddress());

        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);

        return toLoginResponseDTO(member);
    }

    @Override
    // 소셜 로그인
    public LoginResponseDTO loginBySocial(Provider provider, String providerId) {

        // provider와 발급한 providerId를 받아 연동된 계정이 있는지 조회
        // 없다면 예외 발생
        SocialLogin socialLogin = socialLoginRepository.findByProviderAndProviderId(provider, providerId)
                .orElseThrow(() -> new IllegalArgumentException("연동된 계정을 찾을 수 없습니다."));

        // SocialLogin과 연결되어 있는 회원 정보 저장
        Member member = socialLogin.getMember();

        // 탈퇴 기록이 있다면 연동 정보와 무관하게 로그인 불가
        if (member.getDeletedAt() != null) {
            throw new IllegalArgumentException("연동된 계정을 찾을 수 없습니다.");
        }

        // 회원 상태 확인
        switch (member.getStatus()) {
            case DORMANT -> throw new MemberDormantException(); // 휴면 예외
            case SUSPENDED -> throw new MemberSuspendedException(); // 정지 예외
            default -> { } // 그 외에는 로그인 진행
        }

        // 일반 로그인 DTO와 동일한 형태로 반환
        return toLoginResponseDTO(member);
    }

    @Override
    // 이메일이 중복된 계정 여부 확인
    public boolean hasJobSeekerAccountWithEmail(String email) {
        return memberRepository.existsByEmailAndDeletedAtIsNullAndRoleIn(email, JOB_SEEKER_ROLES);
    }

    @Transactional
    @Override
    public LoginResponseDTO signUpSocial(SocialAuthSessionDTO authInfo, SocialSignUpRequestDTO requestDTO) {

        boolean isJobEquip = hasVerifiedBusinessNumber(
                requestDTO.getBusinessNumber(),
                requestDTO.getVerificationToken()
        );

        Member member = buildMember(
                generateSocialUsername(authInfo.getProvider()),
                null,
                requestDTO.getName(),
                requestDTO.getPhone(),
                requestDTO.getEmail(),
                isJobEquip ? MemberRole.JOB_EQUIP : MemberRole.JOB_SEEKER
        );
        member.setSocialSignUp(true);
        member = memberRepository.save(member);

        jobSeekerProfileRepository.save(JobSeekerProfile.builder()
                .member(member)
                .gender(requestDTO.getGender())
                .birthDate(requestDTO.getBirthDate())
                .streetAddress(requestDTO.getStreetAddress())
                .detailAddress(requestDTO.getDetailAddress())
                .build());

        if (isJobEquip) {
            saveBusinessRegistration(member, requestDTO.getBusinessNumber());
        }

        socialLoginRepository.save(SocialLogin.builder()
                .member(member)
                .provider(authInfo.getProvider())
                .providerId(authInfo.getProviderId())
                .linkedAt(LocalDateTime.now())
                .build());

        return toLoginResponseDTO(member);
    }

    @Override
    public boolean verifyPassword(Long memberId, String rawPassword) {
        String passwordHash = memberRepository.findPasswordHashById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        return passwordEncoder.matches(rawPassword, passwordHash);
    }

    @Transactional
    @Override
// JOB_EQUIP → JOB_SEEKER 전환 (사업자번호 삭제)
    public LoginResponseDTO removeBusinessNumber(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        if (member.getRole() != MemberRole.JOB_EQUIP) {
            throw new IllegalStateException("JOB_EQUIP만 사업자번호를 삭제할 수 있습니다.");
        }

        businessRegistrationRepository.deleteById(member.getId());
        member.setRole(MemberRole.JOB_SEEKER);
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);

        return toLoginResponseDTO(member);
    }

    @Transactional
    @Override
    public void changePassword(Long memberId, String currentPassword, String newPassword, String newPasswordConfirm) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 1. 현재 비밀번호 입력 일치 여부
        if (!passwordEncoder.matches(currentPassword, member.getPasswordHash())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 2. 현재 비밀번호와 새 비밀번호 중복 여부
        if (currentPassword.equals(newPassword)) {
            throw new IllegalArgumentException("현재 비밀번호와 다른 새 비밀번호를 입력해주세요.");
        }

        // 3. 새 비밀번호 재입력 일치 여부
        if (!newPassword.equals(newPasswordConfirm)) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
        }

        member.setPasswordHash(passwordEncoder.encode(newPassword));
        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);
    }

    // 소셜 회원 아이디 생성 (사용자에게 노출/입력되지 않는 내부용 값)
    private String generateSocialUsername(Provider provider) {
        return provider.name().charAt(0) + "-" + UUID.randomUUID();
    }

    // JOB_SEEKER는 신규 등록+전환, JOB_EQUIP은 기존 등록정보 갱신
    private void registerOrUpdateBusinessNumber(Member member, String businessNumber, String verificationToken) {
        if (member.getRole() != MemberRole.JOB_SEEKER && member.getRole() != MemberRole.JOB_EQUIP) {
            throw new IllegalStateException("JOB_SEEKER/JOB_EQUIP만 사업자번호를 등록할 수 있습니다.");
        }

        if (!businessNumberTokenProvider.verify(businessNumber, verificationToken)) {
            throw new IllegalArgumentException("사업자번호 인증이 유효하지 않습니다.");
        }

        if (member.getRole() == MemberRole.JOB_SEEKER) {
            saveBusinessRegistration(member, businessNumber);
            member.setRole(MemberRole.JOB_EQUIP);
        } else {
            BusinessRegistration registration = businessRegistrationRepository.findById(member.getId())
                    .orElseThrow(() -> new IllegalArgumentException("사업자등록 정보를 찾을 수 없습니다."));
            registration.setBusinessNumber(businessNumber);
            registration.setVerifiedAt(LocalDateTime.now());
            businessRegistrationRepository.save(registration);
        }
    }

    // 아이디 중복 체크 메서드
    private void validateUsernameNotTaken(String username) {
        if (memberRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
    }

    // 사업자번호 인증 확인
    // businessNumber가 없으면 false, 있으면 토큰을 검증하고 통과 못 하면 예외를 던진다.
    private boolean hasVerifiedBusinessNumber(String businessNumber, String verificationToken) {
        if (businessNumber == null || businessNumber.isBlank()) return false;

        if (!businessNumberTokenProvider.verify(businessNumber, verificationToken)) {
            throw new IllegalArgumentException("사업자번호 인증이 유효하지 않습니다.");
        }
        return true;
    }

    // 공통 회원 정보 생성
    private Member buildMember(
            String username, String rawPassword, String name,
            String phone, String email, MemberRole role
    ) {
        return Member.builder()
                .username(username)
                .passwordHash(rawPassword != null ? passwordEncoder.encode(rawPassword) : null)
                .name(name)
                .phone(phone)
                // TODO phoneVerified는 전화번호 인증 추가 후 삭제
                .phoneVerified(true)
                .email(email)
                .role(role)
                .status(MemberStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // 사업자 정보 저장
    private void saveBusinessRegistration(Member member, String businessNumber) {
        businessRegistrationRepository.save(BusinessRegistration.builder()
                .member(member)
                .businessNumber(businessNumber)
                .verified(true)
                .verifiedAt(LocalDateTime.now())
                .build());
    }

    // Member Entity를 로그인 응답 DTO로 변환
    private LoginResponseDTO toLoginResponseDTO(Member member) {
        return LoginResponseDTO.builder()
                .id(member.getId())
                .username(member.getUsername())
                .name(member.getName())
                .role(member.getRole())
                .build();
    }

}