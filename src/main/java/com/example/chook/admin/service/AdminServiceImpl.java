package com.example.chook.admin.service;

import com.example.chook.admin.dto.JobSeekerTableDTO;
import com.example.chook.admin.mapper.AdminMapper;
import com.example.chook.admin.record.JobSeekerSearchCondition;
import com.example.chook.admin.repository.AdminMemberRepository;
import com.example.chook.member.entity.Member;
import com.example.chook.member.entity.MemberSuspension;
import com.example.chook.member.entity.SocialLogin;
import com.example.chook.member.entity.enums.MemberStatus;
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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

  private final AdminMemberRepository adminMemberRepository;
  private final SocialLoginRepository socialLoginRepository;
  private final MemberRepository memberRepository;
  private final MemberSuspensionRepository memberSuspensionRepository;
  private final AdminMapper adminMapper;


  @Override
  public Page<JobSeekerTableDTO> getPage(int pageIdx, int pageSize, JobSeekerSearchCondition condition) {
    Pageable pageable = PageRequest.of(pageIdx - 1, pageSize);
    Page<JobSeekerTableDTO> result = adminMemberRepository.getPage(pageable, condition);
    List<Long> memberIdList = result.stream().map(JobSeekerTableDTO::getId).toList();
    List<SocialLogin> socialLoginList = socialLoginRepository.findAllByMember_IdIn(memberIdList);

    log.info("socialLoginList={}", socialLoginList);

    Map<Long, List<SocialLogin>> socialLoginListGroupedByMemberId =
      socialLoginList.stream().collect(
        Collectors.groupingBy(SocialLogin -> SocialLogin.getMember().getId()));

    log.info("socialLoginListGroupedByMemberId: {}", socialLoginListGroupedByMemberId);

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

  @Transactional
  @Override
  public void removePhoneVerification(Long memberId) {
    Member member = memberRepository.findById(memberId).orElseThrow(() ->
      new EntityNotFoundException("Member with id: " + memberId + " not found")
    );
    member.setPhoneVerified(false);
    member.setStatus(MemberStatus.SUSPENDED);
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
    if (suspension == null) suspension = new MemberSuspension();
    suspension.setReason(reason);

    // 저장
    member.setStatus(MemberStatus.SUSPENDED);
    memberSuspensionRepository.save(suspension);
    return true;
  }

  @Transactional
  @Override
  public boolean unsuspendMember(Long memberId) {
    if (memberRepository.existsById(memberId))
      throw new EntityNotFoundException("Member with id: " + memberId + " not found");
    boolean isSuspended = memberSuspensionRepository.existsById(memberId);
    if (!isSuspended) return false;
    memberSuspensionRepository.deleteById(memberId);
    return true;
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

}
