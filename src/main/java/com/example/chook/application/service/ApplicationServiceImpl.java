package com.example.chook.application.service;

import com.example.chook.application.dto.ApplicationCategoryListDTO;
import com.example.chook.application.dto.ApplicationDTO;
import com.example.chook.application.dto.ApplyDTO;
import com.example.chook.application.entity.Application;
import com.example.chook.application.entity.enums.ApplicationResult;
import com.example.chook.application.repository.ApplicationRepository;
import com.example.chook.festival.Festival;
import com.example.chook.festival.FestivalRepository;
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

    // 구인자(username)가 등록한 행사 목록 조회
    private final FestivalRepository festivalRepository;

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

    // 내가 지원한 목록 조회 (구직자용)
    @Override
    public List<ApplicationDTO> getList(Long memberId) {

        List<Application> applicationList = applicationRepository.findByMemberId(memberId);

        return applicationList.stream()
                .map(this::convertEntityToDto)
                .toList();
    }

    // 특정 모집공고에 지원한 구직자 목록 조회 (구인자용)
    @Override
    public List<ApplicationDTO> getApplicants(Long recruitmentId) {
        List<Application> applicationList =
                applicationRepository.findByRecruitmentId(recruitmentId);

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
        Resume resume = resumeRepository.findByMemberId(memberId)
                .orElse(null);

        ResumeResponseDTO resumeResponseDTO = null;

        if (resume != null) {
            resumeResponseDTO = resumeService.getResumeByMemberId(memberId);
        }

        // 회원 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 변환 메서드 호출
        return convertToApplyDto(member, recruitment, resumeResponseDTO);
    }

    // 구인자가 등록한 모집공고 중 선택한 카테고리의 지원자 목록 조회
    @Override
    public ApplicationCategoryListDTO getApplicantsByRecruiter(String username) {

        // 구인자가 등록한 행사 조회
        List<Festival> festivals =
                festivalRepository.findByMember_Username(username);

        // 카테고리별 지원자 목록
        List<ApplicationDTO> individualApplications = new ArrayList<>();
        List<ApplicationDTO> foodTruckApplications = new ArrayList<>();
        List<ApplicationDTO> equipmentApplications = new ArrayList<>();
        List<ApplicationDTO> etcApplications = new ArrayList<>();

        // 구인자가 등록한 모든 행사
        for (Festival festival : festivals){

            // 해당 행사에 등록된 모든 모집공고
            for (Recruitment recruitment : festival.getRecruitments()){

                // 해당 모집공고에 지원한 지원자 조회
                List<Application> applications = applicationRepository.findByRecruitmentId(recruitment.getId());

                List<ApplicationDTO> applicationDTOList = applications.stream()
                        .map(this::convertEntityToDto)
                        .toList();
                // 카테고리별 분류
                switch (recruitment.getCategory()){
                    case INDIVIDUAL ->
                            individualApplications.addAll(applicationDTOList);

                    case FOOD_TRUCK ->
                            foodTruckApplications.addAll(applicationDTOList);

                    case EQUIPMENT ->
                            equipmentApplications.addAll(applicationDTOList);

                    case ETC ->
                            etcApplications.addAll(applicationDTOList);
                }
            }
        }
        // 4개 목록을 하나로 묶어서 반환
        return ApplicationCategoryListDTO.builder()
                .individualApplications(individualApplications)
                .foodTruckApplications(foodTruckApplications)
                .equipmentApplications(equipmentApplications)
                .etcApplications(etcApplications)
                .build();
    }
}