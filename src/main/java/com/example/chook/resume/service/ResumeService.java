package com.example.chook.resume.service;

import com.example.chook.file.entity.UploadedFile;
import com.example.chook.member.entity.Member;
import com.example.chook.resume.dto.*;
import com.example.chook.resume.entity.*;

public interface ResumeService {


    // Resume(Entity) → ResumeResponseDTO
    default ResumeResponseDTO resumeEntityToDto(
            Resume resume
    ){
        return ResumeResponseDTO.builder()
                .id(resume.getId())
                .memberId(resume.getMember().getId())
                .introduction(resume.getIntroduction())
                .savedAt(resume.getSavedAt())
                .build();
    }

    // ResumeRequestDTO → Resume(Entity)
    default Resume resumeDtoToEntity(
            ResumeRequestDTO resumeRequestDTO,
            Member member
    ){
        return Resume.builder()
                .member(member)
                .introduction(resumeRequestDTO.getIntroduction())
                .build();
    }

    // ProfileFile(Entity) -> ProfileFileDTO
    default ProfileFileDTO profileFileEntityToDto(
            ProfileFile profileFile
    ){
        return ProfileFileDTO.builder()
                .id(profileFile.getId())
                .uuid(profileFile.getUploadedFile().getUuid())
                .originalName(profileFile.getUploadedFile().getOriginalName())
                .build();
    }

    // ProfileFileDTO → profileFile(Entity)
    default ProfileFile profileFileDtoToEntity(
            ProfileFileDTO profileFileDTO,
            UploadedFile uploadedFile,
            Resume resume
    ){

        return ProfileFile.builder()
                .id(profileFileDTO.getId())
                .resume(resume)
                .uploadedFile(uploadedFile)
                .build();
    }

    // ResumeCareer(Entity) -> ResumeCareerDTO
    default ResumeCareerDTO resumeCareerEntityToDto(
            ResumeCareer resumeCareer
    ){
        return ResumeCareerDTO.builder()
                .id(resumeCareer.getId())
                .careerName(resumeCareer.getCareerName())
                .startDate(resumeCareer.getStartDate())
                .endDate(resumeCareer.getEndDate())
                .duties(resumeCareer.getDuties())
                .build();
    }

    // ResumeCareerDTO → ResumeCareer(Entity)
    default ResumeCareer resumeCareerDtoToEntity(
            ResumeCareerDTO resumeCareerDTO,
            Resume resume
    ){
        return ResumeCareer.builder()
                .id(resumeCareerDTO.getId())
                .resume(resume)
                .careerName(resumeCareerDTO.getCareerName())
                .startDate(resumeCareerDTO.getStartDate())
                .endDate(resumeCareerDTO.getEndDate())
                .duties(resumeCareerDTO.getDuties())
                .build();
    }

    // ResumeFile(Entity) -> ResumeFileDTO
    default ResumeFileDTO resumeFileEntityToDto(
            ResumeFile resumeFile
    ){
        return ResumeFileDTO.builder()
                .id(resumeFile.getId())
                .uuid(resumeFile.getUploadedFile().getUuid())
                .originalName(resumeFile.getUploadedFile().getOriginalName())
                .build();
    }

    // ResumeFileDTO -> ResumeFile(Entity)
    default ResumeFile resumeFileDtoToEntity(
            ResumeFileDTO resumeFileDTO,
            ResumePortfolio resumePortfolio,
            UploadedFile uploadedFile
    ){
            return ResumeFile.builder()
                    .id(resumeFileDTO.getId())
                    .resumePortfolio(resumePortfolio)
                    .uploadedFile(uploadedFile)
                    .build();
    }

    // ResumePortfolio(Entity) -> ResumePortfolioDTO
    default ResumePortfolioDTO resumePortfolioEntityToDto(
            ResumePortfolio resumePortfolio
    ){
        return ResumePortfolioDTO.builder()
                .id(resumePortfolio.getId())
                .type(resumePortfolio.getType())
                .title(resumePortfolio.getTitle())
                .registeredAt(resumePortfolio.getRegisteredAt())
                .build();
    }

    // ResumePortfolioDTO → ResumePortfolio(Entity)
    default ResumePortfolio resumePortfolioDtoToEntity(
        ResumePortfolioDTO resumePortfolioDTO,
        Resume resume
    ){
        return ResumePortfolio.builder()
                .id(resumePortfolioDTO.getId())
                .type(resumePortfolioDTO.getType())
                .title(resumePortfolioDTO.getTitle())
                .registeredAt(resumePortfolioDTO.getRegisteredAt())
                .build();
    }

    // 이력서 등록하기 기능
    void register(ResumeRequestDTO resumeRequestDTO, Long memberId);

    // 이력서 조회 기능
    ResumeResponseDTO getResume(Long memberId);

    // 이력서 수정
    void modify(ResumeRequestDTO resumeRequestDTO, Long memberId);

    // 이력서 삭제
    void delete(Long memberId);
}