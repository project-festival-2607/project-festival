package com.example.chook.application.service;

import com.example.chook.application.dto.ApplicationDTO;
import com.example.chook.application.dto.ApplyDTO;
import com.example.chook.application.entity.Application;
import com.example.chook.application.entity.enums.ApplicationResult;
import com.example.chook.application.repository.ApplicationRepository;
import com.example.chook.member.entity.Member;
import com.example.chook.member.repository.MemberRepository;
import com.example.chook.recruitment.dto.RecruitmentResponseDTO;
import com.example.chook.recruitment.entity.Recruitment;
import com.example.chook.recruitment.repository.RecruitmentRepository;
import com.example.chook.recruitment.service.RecruitmentService;
import com.example.chook.resume.dto.ResumeResponseDTO;
import com.example.chook.resume.entity.Resume;
import com.example.chook.resume.repository.ResumeRepository;
import com.example.chook.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    // 조회용
    private final MemberRepository memberRepository; // 회원 ID로 Member 가져오기
    private final RecruitmentRepository recruitmentRepository; // 공고 ID로 Recruitment 가져오기
    private final ResumeRepository resumeRepository; // 이력서 ID로 Resume 가져오기

    // 최종 저장용
    private final ApplicationRepository applicationRepository; // 완성된 Application 저장하기

    // applypage Zone
    private final RecruitmentService recruitmentService;
    private final ResumeService resumeService;

    // 지원하기 기능
    @Override
    public Long apply(ApplicationDTO applicationDTO) {

        Member member = memberRepository.findById(applicationDTO.getMemberId())
                .orElseThrow();

        Recruitment recruitment = recruitmentRepository.findById(applicationDTO.getRecruitmentId())
                .orElseThrow();

        Resume resume = resumeRepository.findById(applicationDTO.getResumeId())
                .orElseThrow();

        Application application = Application.builder()
                .member(member)
                .recruitment(recruitment)
                .resume(resume)
                .registerDate(LocalDateTime.now())
                .result(ApplicationResult.APPLIED)
                .build();

        applicationRepository.save(application);

        return application.getId();
    }

    // 내가 지원한 목록 조회
    @Override
    public List<ApplicationDTO> getList(Long memberId) {

        List<Application> applicationList = applicationRepository.findByMemberId(memberId);

        return applicationList.stream()
                .map(this::convertEntityToDto)
                .toList();
    }

    // 지원 상세 조회
    @Override
    public ApplicationDTO getDetail(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow();

        return convertEntityToDto(application);
    }

    // 지원 취소
    @Override
    public void cancel(Long id) {
        applicationRepository.deleteById(id);
    }

    // 합격/불합격 처리
    @Override
    public void updateResult(Long id, ApplicationResult result) {

        Application application = applicationRepository.findById(id)
                .orElseThrow();

        application.setResult(result);

        applicationRepository.save(application);
    }

    // 지원서 열람 처리
    @Override
    public void read(Long id) {

        Application application = applicationRepository.findById(id)
                .orElseThrow();

        if (application.getReadDate() == null){

            application.setReadDate(LocalDateTime.now());

            applicationRepository.save(application);
        }
    }

    // applypage Zone
    @Override
    public ApplyDTO getApplyData(Long recruitmentId, Long memberId) {

        // 공고 정보 조회
        RecruitmentResponseDTO recruitment = recruitmentService.getRecruitment(recruitmentId);

        // 이력서 정보 조회
        Resume resume = resumeRepository.getResumeByMemberId(memberId);

        ResumeResponseDTO resumeResponseDTO = null;

        if (resume != null) {
            resumeResponseDTO = resumeService.resumeEntityToDto(resume);
        }

        // 회원 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 변환 메서드 호출
        return convertToApplyDto(member, recruitment, resumeResponseDTO);
    }
}
