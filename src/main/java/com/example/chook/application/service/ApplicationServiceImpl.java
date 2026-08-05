package com.example.chook.application.service;

import com.example.chook.application.dto.ApplicationDTO;
import com.example.chook.application.entity.Application;
import com.example.chook.application.entity.enums.ApplicationResult;
import com.example.chook.application.repository.ApplicationRepository;
import com.example.chook.member.entity.Member;
import com.example.chook.member.repository.MemberRepository;
import com.example.chook.recruitment.entity.Recruitment;
import com.example.chook.recruitment.repository.RecruitmentRepository;
import com.example.chook.resume.entity.Resume;
import com.example.chook.resume.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class ApplicationServiceImpl implements ApplicationService {

    // 조회용
    private final MemberRepository memberRepository; // 회원 ID로 Member 가져오기
    private final RecruitmentRepository recruitmentRepository; // 공고 ID로 Recruitment 가져오기
    private final ResumeRepository resumeRepository; // 이력서 ID로 Resume 가져오기

    // 최종 저장용
    private final ApplicationRepository applicationRepository; // 완성된 Application 저장하기

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

}
