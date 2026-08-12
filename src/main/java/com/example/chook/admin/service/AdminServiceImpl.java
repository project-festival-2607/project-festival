package com.example.chook.admin.service;

import com.example.chook.admin.dto.JobSeekerTableDTO;
import com.example.chook.admin.record.JobSeekerSearchCondition;
import com.example.chook.admin.repository.AdminMemberRepository;
import com.example.chook.member.entity.Member;
import com.example.chook.member.entity.MemberSuspension;
import com.example.chook.member.entity.enums.MemberStatus;
import com.example.chook.member.repository.MemberRepository;
import com.example.chook.member.repository.MemberSuspensionRepository;
import com.example.chook.payment.repository.PointHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminServiceImpl implements AdminService {

  private final AdminMemberRepository adminMemberRepository;
  private final MemberRepository memberRepository;
  private final PointHistoryRepository pointHistoryRepository;
  private final MemberSuspensionRepository memberSuspensionRepository;


  @Override
  public Page<JobSeekerTableDTO> getPage(int pageIdx, int pageSize, JobSeekerSearchCondition condition) {
    Pageable pageable = PageRequest.of(pageIdx - 1, pageSize);
    return adminMemberRepository.getPage(pageable, condition);
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
    Member member = memberRepository.findById(memberId).orElseThrow(() ->
      new EntityNotFoundException("Member with id: " + memberId + " not found")
    );
    MemberStatus currentStatus = member.getStatus();
    String currentSuspendedReason = memberSuspensionRepository.findById(memberId)
      .map(MemberSuspension::getReason)
      .orElse(null);

    if (reason == null) throw new IllegalArgumentException("사유를 입력해 주시기 바랍니다.");
    if (currentStatus == MemberStatus.SUSPENDED && reason.equals(currentSuspendedReason)) {
      return false;
    }
    member.setStatus(MemberStatus.SUSPENDED);
    memberSuspensionRepository.save(
      MemberSuspension.builder()
        .member(member)
        .reason(reason)
        .build()
    );
    return true;
  }

  @Override
  public boolean unsuspendMember(Long memberId) {
    if (memberRepository.existsById(memberId))
      throw new EntityNotFoundException("Member with id: " + memberId + " not found");
    boolean isSuspended = memberSuspensionRepository.existsById(memberId);
    if (!isSuspended) return false;
    memberSuspensionRepository.deleteById(memberId);
    return true;
  }

}
