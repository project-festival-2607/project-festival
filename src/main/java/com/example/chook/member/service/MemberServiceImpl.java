package com.example.chook.member.service;

import com.example.chook.member.BusinessNumberTokenProvider;
import com.example.chook.member.dto.*;
import com.example.chook.member.entity.BusinessRegistration;
import com.example.chook.member.entity.EmployerProfile;
import com.example.chook.member.entity.JobSeekerProfile;
import com.example.chook.member.entity.Member;
import com.example.chook.member.entity.enums.MemberRole;
import com.example.chook.member.entity.enums.MemberStatus;
import com.example.chook.member.exception.MemberDormantException;
import com.example.chook.member.exception.MemberSuspendedException;
import com.example.chook.member.repository.BusinessRegistrationRepository;
import com.example.chook.member.repository.EmployerProfileRepository;
import com.example.chook.member.repository.JobSeekerProfileRepository;
import com.example.chook.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

    @Override
    public LoginResponseDTO login(LoginRequestDTO requestDTO) {

        // 입력받은 username으로 회원 조회
        // 탈퇴한 회원은 로그인 불가능(DeletedAtNull)
        Member member = memberRepository.findByUsernameAndDeletedAtIsNull(requestDTO.getUsername())
                .filter(m -> passwordEncoder.matches(requestDTO.getPassword(), m.getPasswordHash()))
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다."));

        // 회원 status에 따라 로그인 제한
        switch (member.getStatus()) {
            case DORMANT -> throw new MemberDormantException(); // 휴면 예외
            case SUSPENDED -> throw new MemberSuspendedException(); // 정지 예외
            default -> {}
        }
        // Entity를 로그인응답 DTO로 변환하여 반환
        return toLoginResponseDTO(member);
    }

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
    public LoginResponseDTO updateProfile(Long memberId, JobSeekerProfileUpdateRequestDTO requestDTO) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        JobSeekerProfile profile = jobSeekerProfileRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("프로필을 찾을 수 없습니다."));

        member.setEmail(requestDTO.getEmail());
        // phone/phoneVerified 갱신은 전화번호 인증 서비스 구현 후 추가

        profile.setStreetAddress(requestDTO.getStreetAddress());
        profile.setDetailAddress(requestDTO.getDetailAddress());

        if (requestDTO.getBusinessNumber() != null && !requestDTO.getBusinessNumber().isBlank()) {
            registerBusinessNumber(member, requestDTO.getBusinessNumber(), requestDTO.getVerificationToken());
        }

        member.setUpdatedAt(LocalDateTime.now());
        memberRepository.save(member);

        return toLoginResponseDTO(member);
    }

    // JOB_SEEKER 회원에게 사업자번호를 등록하고 JOB_EQUIP으로 전환한다.
    private void registerBusinessNumber(Member member, String businessNumber, String verificationToken) {
        if (member.getRole() != MemberRole.JOB_SEEKER) {
            throw new IllegalStateException("JOB_SEEKER만 사업자번호를 추가할 수 있습니다.");
        }

        if (!businessNumberTokenProvider.verify(businessNumber, verificationToken)) {
            throw new IllegalArgumentException("사업자번호 인증이 유효하지 않습니다.");
        }

        saveBusinessRegistration(member, businessNumber);
        member.setRole(MemberRole.JOB_EQUIP);
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
                .passwordHash(passwordEncoder.encode(rawPassword))
                .name(name)
                .phone(phone)
                // TODO phoneVerified는 전화번호 인증 추가 후 삭제
                .phoneVerified(true)
                .email(email)
                .role(role)
                .status(MemberStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .point(0L)
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