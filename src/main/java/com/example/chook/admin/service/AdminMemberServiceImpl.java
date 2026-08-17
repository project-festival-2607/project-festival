package com.example.chook.admin.service;

import com.example.chook.admin.condition.member.JobEquipSearchCondition;
import com.example.chook.admin.condition.member.JobSeekerSearchCondition;
import com.example.chook.admin.condition.member.RecruiterSearchCondition;
import com.example.chook.admin.dto.JobEquipTableDTO;
import com.example.chook.admin.dto.JobSeekerTableDTO;
import com.example.chook.admin.dto.JobSeekerTableDTOBase;
import com.example.chook.admin.dto.RecruiterTableDTO;
import com.example.chook.admin.mapper.AdminMapper;
import com.example.chook.admin.repository.AdminMemberRepository;
import com.example.chook.member.entity.BusinessRegistration;
import com.example.chook.member.entity.Member;
import com.example.chook.member.entity.MemberSuspension;
import com.example.chook.member.entity.SocialLogin;
import com.example.chook.member.entity.enums.MemberRole;
import com.example.chook.member.entity.enums.MemberStatus;
import com.example.chook.member.repository.BusinessRegistrationRepository;
import com.example.chook.member.repository.MemberRepository;
import com.example.chook.member.repository.MemberSuspensionRepository;
import com.example.chook.member.repository.SocialLoginRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminMemberServiceImpl implements AdminMemberService {

  private final AdminMemberRepository adminMemberRepository;
  private final AdminMapper adminMapper;

  private final MemberRepository memberRepository;
  private final MemberSuspensionRepository memberSuspensionRepository;

  private final SocialLoginRepository socialLoginRepository;
  private final BusinessRegistrationRepository businessRegistrationRepository;


  @Override
  public Page<JobSeekerTableDTO> getJobSeekerPage(int pageIdx, int pageSize, JobSeekerSearchCondition condition) {
    Pageable pageable = PageRequest.of(pageIdx - 1, pageSize);
    Page<JobSeekerTableDTO> result = adminMemberRepository.getJobSeekerPage(pageable, condition);
    return getDtoWithSocialLogins(result);
  }

  @Override
  public Page<JobEquipTableDTO> getJobEquipPage(int pageIdx, int pageSize, JobEquipSearchCondition condition) {
    Pageable pageable = PageRequest.of(pageIdx - 1, pageSize);
    Page<JobEquipTableDTO> result = adminMemberRepository.getJobEquipPage(pageable, condition);
    return getDtoWithSocialLogins(result);
  }

  @Override
  public Page<RecruiterTableDTO> getRecruiterPage(int pageIdx, int pageSize, RecruiterSearchCondition condition) {
    Pageable pageable = PageRequest.of(pageIdx - 1, pageSize);
    return adminMemberRepository.getRecruiterPage(pageable, condition);
  }

  @Transactional
  @Override
  public boolean suspendMember(Long memberId, String reason) {

    // 입력 검증
    Member member = memberRepository.findById(memberId).orElseThrow(() ->
      new EntityNotFoundException("Member with id: " + memberId + " not found")
    );
    if (reason == null) throw new IllegalArgumentException("사유를 입력해 주시기 바랍니다.");

    // 기존 데이터 검증
    MemberSuspension suspension = memberSuspensionRepository.findById(memberId).orElse(null);
    String currentSuspendedReason = (suspension == null) ? null : suspension.getReason();

    // 입력 검증 (현재 정지되어 있으며 사유가 기존과 같으면 update하지 않음)
    if (suspension != null && reason.equals(currentSuspendedReason)) return false;

    // 개체 (생성 후) 업데이트
    if (suspension == null) {
      suspension = new MemberSuspension();
      suspension.setMember(member);
    }
    suspension.setReason(reason);

    // 저장
    member.setStatus(MemberStatus.SUSPENDED);
    memberSuspensionRepository.save(suspension);
    return true;
  }

  @Transactional
  @Override
  public boolean unsuspendMember(Long memberId) {
    Member member = memberRepository.findById(memberId).orElseThrow(() ->
      new EntityNotFoundException("Member with id: " + memberId + " not found")
    );
    // Member 공통
    if (!member.isPhoneVerified())
      throw new IllegalStateException("휴대전화번호가 인증된 상태여야만 정지를 해제할 수 있습니다.");
    // RECRUITER 한정
    if (member.getRole() == MemberRole.RECRUITER && !businessRegistrationRepository.existsById(memberId)) {
      throw new IllegalStateException("사업자등록번호가 인증된 상태여야만 정지를 해제할 수 있습니다.");
    }
    boolean isSuspended = memberSuspensionRepository.existsById(memberId);
    if (!isSuspended) return false;
    memberSuspensionRepository.deleteById(memberId);
    member.setStatus(MemberStatus.ACTIVE);
    return true;
  }

  @Transactional
  @Override
  public void removePhoneVerification(Long memberId) {
    Member member = memberRepository.findById(memberId).orElseThrow(() ->
      new EntityNotFoundException("Member with id: " + memberId + " not found")
    );
    member.setPhoneVerified(false);
    suspendMember(memberId, "휴대전화번호 인증이 유효하지 않음");
  }

  @Transactional
  @Override
  public boolean addPhoneWithVerification(Long memberId, String phone) {
    Member member = memberRepository.findById(memberId).orElseThrow(() ->
      new EntityNotFoundException("Member with id: " + memberId + " not found")
    );
    if (member.isPhoneVerified()) return false;
    member.setPhone(phone);
    member.setPhoneVerified(true);
    return true;
  }

  @Transactional
  @Override
  public boolean removeBusinessRegistration(Long memberId) {
    Member member = memberRepository.findById(memberId).orElseThrow(() ->
      new EntityNotFoundException("Member with id: " + memberId + " not found")
    );
    if (!businessRegistrationRepository.existsById(memberId)) {
      throw new IllegalArgumentException("Member with id: " + memberId + " doesn't have business registration");
    }
    businessRegistrationRepository.deleteById(memberId);
    // RECRUITER의 경우 반드시 사업자등록번호를 가져야하므로 정지
    // JOB_EQUIP의 경우 역할만 JOB_SEEKER로 변경
    if (member.getRole() == MemberRole.RECRUITER) {
      suspendMember(memberId, "사업자등록번호 인증이 유효하지 않음");
      return true;
    } else {
      member.setRole(MemberRole.JOB_SEEKER);
      return false;
    }
  }

  @Transactional
  @Override
  public boolean addBusinessRegistration(Long memberId, String businessNumber) {
    Member member = memberRepository.findById(memberId).orElseThrow(() ->
      new EntityNotFoundException("Member with id: " + memberId + " not found")
    );
    if (businessRegistrationRepository.existsById(memberId)) return false;
    businessRegistrationRepository.save(BusinessRegistration.builder()
      .member(member)
      .businessNumber(businessNumber)
      .verified(true)
      .verifiedAt(LocalDateTime.now())
      .build());
    if (member.getRole() == MemberRole.JOB_SEEKER) member.setRole(MemberRole.JOB_EQUIP);
    return true;
  }

  private <T extends JobSeekerTableDTOBase> Page<T> getDtoWithSocialLogins(Page<T> result) {

    List<Long> memberIdList = result.stream().map(T::getId).toList();
    List<SocialLogin> socialLoginList = socialLoginRepository.findAllByMember_IdIn(memberIdList);

    Map<Long, List<SocialLogin>> socialLoginListGroupedByMemberId =
      socialLoginList.stream().collect(
        Collectors.groupingBy(SocialLogin -> SocialLogin.getMember().getId()));

    result.forEach(dto -> {
      dto.setSocialLoginDtoList(
        socialLoginListGroupedByMemberId
          .getOrDefault(dto.getId(), List.of())
          .stream()
          .sorted(Comparator.comparing(SocialLogin::getProvider))
          .map(adminMapper::toDto)
          .toList()
      );
    });

    return result;
  }

}
