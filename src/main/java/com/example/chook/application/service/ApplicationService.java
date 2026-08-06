package com.example.chook.application.service;

import com.example.chook.application.dto.ApplicationDTO;
import com.example.chook.application.entity.Application;
import com.example.chook.application.entity.enums.ApplicationResult;
import com.example.chook.member.entity.Member;
import com.example.chook.recruitment.entity.Recruitment;
import com.example.chook.resume.entity.Resume;

import java.util.List;


public interface ApplicationService {

    // DTO → Entity 변환
    default Application convertDtoToEntity(
            ApplicationDTO applicationDTO,
            Member member,
            Recruitment recruitment,
            Resume resume
    ){
        return Application.builder()
                .id(applicationDTO.getId())
                .member(member)
                .recruitment(recruitment)
                .resume(resume)
                .registerDate(applicationDTO.getRegisterDate())
                .result(applicationDTO.getResult())
                .build();
    }

    // Entity → DTO 변환
    default ApplicationDTO convertEntityToDto(Application application){
        return ApplicationDTO.builder()
                .id(application.getId())
                .memberId(application.getMember().getId())
                .recruitmentId(application.getRecruitment().getId())
                .resumeId(application.getResume().getId())
                .registerDate(application.getRegisterDate())
                .readDate(application.getReadDate())
                .build();
    }

    // 지원하기 기능
    Long apply(ApplicationDTO applicationDTO);

    // 내가 지원한 목록 조회
    List<ApplicationDTO> getList(Long memberId);

    // 지원 상세 조회
    ApplicationDTO getDetail(Long id);

    // 지원 취소
    void cancel(Long id);

    // 합격/불합격 처리
    void updateResult(Long id, ApplicationResult result);

    // 지원서 열람 처리
    void read(Long id);
}
