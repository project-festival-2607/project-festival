package com.example.chook.resume.service;

import java.time.LocalDateTime;
import com.example.chook.file.entity.UploadedFile;
import com.example.chook.file.repository.UploadedFileRepository;
import com.example.chook.member.entity.Member;
import com.example.chook.member.repository.MemberRepository;
import com.example.chook.resume.dto.*;
import com.example.chook.resume.entity.*;
import com.example.chook.resume.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class ResumeServiceImpl implements ResumeService{

    private final ResumeRepository resumeRepository;
    private final MemberRepository memberRepository;
    private final ProfileFileRepository profileFileRepository;
    private final UploadedFileRepository uploadedFileRepository;
    private final ResumeCareerRepository resumeCareerRepository;
    private final ResumePortfolioRepository resumePortfolioRepository;
    private final ResumeFileRepository resumeFileRepository;

    // 이력서 등록하기 기능
    @Override
    public Long register(ResumeRequestDTO resumeRequestDTO, Long memberId) {
        // 회원 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow();

        Resume resume = resumeDtoToEntity(resumeRequestDTO, member);

        resume.setSavedAt(LocalDateTime.now());

        Resume savedResume = resumeRepository.save(resume);

        // 프로필 파일 저장
        if (resumeRequestDTO.getProfileFileUuid() != null) {

            // UUID로 실제 파일 찾기
            UploadedFile uploadedFile =
                    uploadedFileRepository.findById(
                                    UUID.fromString(resumeRequestDTO.getProfileFileUuid())
                            )
                            .orElseThrow();

            ProfileFile profileFile =
                    ProfileFile.builder()
                            .resume(resume)
                            .uploadedFile(uploadedFile)
                            .build();
            profileFileRepository.save(profileFile);
        }

        // 경력사항 저장
        if (resumeRequestDTO.getCareers() != null) {

            for (ResumeCareerDTO careerDTO : resumeRequestDTO.getCareers()) {

                // DTO → Entity 변환
                ResumeCareer resumeCareer = resumeCareerDtoToEntity(careerDTO, resume);

                resumeCareerRepository.save(resumeCareer);
            }
        }

        // 포트폴리오 저장
        if (resumeRequestDTO.getPortfolios() != null) {

            for (ResumePortfolioDTO portfolioDTO : resumeRequestDTO.getPortfolios()) {

                // 포트폴리오 유형이 없음이면 저장하지 않음
                if (portfolioDTO.getType() == null
                        || portfolioDTO.getType().equals("NONE")) {
                    continue;
                }

                // DTO → Entity 변환
                ResumePortfolio resumePortfolio =
                        resumePortfolioDtoToEntity(
                                portfolioDTO,
                                resume
                        );

                // 포트폴리오 저장
                resumePortfolioRepository.save(resumePortfolio);


                // 포트폴리오 첨부파일 저장
                if (portfolioDTO.getResumeFile() != null) {

                    // UUID로 실제 파일 찾기
                    UploadedFile uploadedFile =
                            uploadedFileRepository.findById(
                                            portfolioDTO.getResumeFile().getUuid()
                                    )
                                    .orElseThrow();


                    // ResumeFile 생성
                    ResumeFile resumeFile =
                            ResumeFile.builder()
                                    .resumePortfolio(resumePortfolio)
                                    .uploadedFile(uploadedFile)
                                    .build();


                    // ResumeFile 저장
                    resumeFileRepository.save(resumeFile);
                }
            }
        }
        return savedResume.getId();
    }

    // 이력서 조회 하기 기능
    @Override
    public ResumeResponseDTO getResume(Long resumeId) {

        // 이력서 조회
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow();

        ResumeResponseDTO resumeResponseDTO = resumeEntityToDto(resume);

        // 경력 조회
        List<ResumeCareer> careers = resumeCareerRepository.findByResume_Id(resume.getId());

        List<ResumeCareerDTO> careerDTOList =
                careers.stream()
                        .map(this::resumeCareerEntityToDto)
                        .toList();
        resumeResponseDTO.setCareers(careerDTOList);

        // 프로필 파일 조회
        ProfileFile profileFile =
                profileFileRepository.findByResume_Id(resume.getId())
                        .orElse(null);

        if(profileFile != null){
            ProfileFileDTO profileFileDTO =
                    profileFileEntityToDto(profileFile);

            resumeResponseDTO.setProfileFile(profileFileDTO);
        }

        // 포트폴리오 조회
        List<ResumePortfolio> portfolios = resumePortfolioRepository.findByResume_Id(resume.getId());

        List<ResumePortfolioDTO> portfolioDTOList = new ArrayList<>();

        for(ResumePortfolio portfolio : portfolios){
            ResumePortfolioDTO portfolioDTO =
                    resumePortfolioEntityToDto(portfolio);

            // 포트폴리오 파일 조회
            ResumeFile resumeFile =
                    resumeFileRepository.findByResumePortfolio_Id(portfolio.getId())
                            .orElse(null);
            if(resumeFile != null){
                ResumeFileDTO resumeFileDTO =
                        resumeFileEntityToDto(resumeFile);

                // 포트폴리오 DTO에 파일 추가
                portfolioDTO.setResumeFile(resumeFileDTO);
            }
            portfolioDTOList.add(portfolioDTO);
        }
        resumeResponseDTO.setPortfolios(portfolioDTOList);

        return resumeResponseDTO;
    }

    // 이력서 수정
    @Override
    public void modify(ResumeRequestDTO resumeRequestDTO, Long resumeId) {
        // 기존 이력서 조회
        Resume resume = resumeRepository.findById(resumeId)
                        .orElseThrow();

        // 자기소개 수정
        resume.setIntroduction(resumeRequestDTO.getIntroduction());

        resume.setSavedAt(LocalDateTime.now());

        resumeRepository.save(resume);

        // 프로필 파일 수정
        if (resumeRequestDTO.getProfileFileUuid() != null && !resumeRequestDTO.getProfileFileUuid().isBlank()) {

            ProfileFile profileFile = profileFileRepository
                            .findByResume_Id(resumeId)
                            .orElseThrow();

            UploadedFile uploadedFile = uploadedFileRepository.findById(
                            UUID.fromString(
                                    resumeRequestDTO
                                            .getProfileFileUuid()
                            )
                    ).orElseThrow();

            profileFile.setUploadedFile(uploadedFile);

            profileFileRepository.save(profileFile);
        }
        // 기존 경력 조회
        List<ResumeCareer> existingCareers = resumeCareerRepository
                        .findByResume_Id(resumeId);

        // 수정 페이지에서 살아있는 기존 경력 ID
        List<Long> careerIds = new ArrayList<>();

        if (resumeRequestDTO.getCareers() != null) {

            for (ResumeCareerDTO careerDTO : resumeRequestDTO.getCareers()) {

                // 기존 경력
                if (careerDTO.getId() != null) {

                    careerIds.add(careerDTO.getId());

                    ResumeCareer resumeCareer = resumeCareerRepository
                                    .findById(careerDTO.getId())
                                    .orElseThrow();

                    resumeCareer.setCareerName(careerDTO.getCareerName());

                    resumeCareer.setStartDate(careerDTO.getStartDate());

                    resumeCareer.setEndDate(careerDTO.getEndDate());

                    resumeCareer.setDuties(careerDTO.getDuties());

                    resumeCareerRepository.save(resumeCareer);

                } else {
                    // 새 경력
                    ResumeCareer newCareer = resumeCareerDtoToEntity(careerDTO, resume);

                    resumeCareerRepository.save(newCareer);
                }
            }
        }
        // 화면에서 삭제한 기존 경력 DB 삭제
        for (ResumeCareer existingCareer : existingCareers) {

            if (!careerIds.contains(existingCareer.getId())) {

                resumeCareerRepository.delete(existingCareer);
            }
        }
        // 기존 포트폴리오 조회
        List<ResumePortfolio> existingPortfolios = resumePortfolioRepository
                        .findByResume_Id(resumeId);

        // 수정 페이지에서 살아있는 기존 포트폴리오 ID
        List<Long> portfolioIds = new ArrayList<>();

        if (resumeRequestDTO.getPortfolios() != null) {

            for (ResumePortfolioDTO portfolioDTO : resumeRequestDTO.getPortfolios()) {

                // 기존 포트폴리오
                if (portfolioDTO.getId() != null) {

                    portfolioIds.add(portfolioDTO.getId());

                    ResumePortfolio resumePortfolio = resumePortfolioRepository
                                    .findById(portfolioDTO.getId())
                                    .orElseThrow();

                    resumePortfolio.setTitle(portfolioDTO.getTitle());

                    resumePortfolio.setType(portfolioDTO.getType());

                    resumePortfolio.setUrl(portfolioDTO.getUrl());

                    resumePortfolioRepository.save(resumePortfolio);

                    // 기존 포트폴리오 파일 수정
                    if (portfolioDTO.getResumeFile() != null) {

                        UploadedFile uploadedFile = uploadedFileRepository
                                .findById(portfolioDTO
                                                .getResumeFile()
                                                .getUuid()
                                ).orElseThrow();

                        ResumeFile resumeFile = resumeFileRepository
                                        .findByResumePortfolio_Id(
                                                resumePortfolio.getId()
                                        )
                                        .orElse(null);

                        if (resumeFile != null) {
                            // 기존 파일 교체
                            resumeFile.setUploadedFile(uploadedFile);

                            resumeFileRepository.save(resumeFile);

                        } else {
                            // 기존 파일이 없으면 새로 생성
                            ResumeFile newResumeFile = ResumeFile.builder()
                                            .resumePortfolio(resumePortfolio)
                                            .uploadedFile(uploadedFile)
                                            .build();
                            resumeFileRepository.save(newResumeFile);
                        }
                    }
                } else {
                    // 새 포트폴리오
                    ResumePortfolio newPortfolio = resumePortfolioDtoToEntity(portfolioDTO, resume);

                    resumePortfolioRepository.save(newPortfolio);
                }
            }
        }
        // 삭제된 포트폴리오 DB 삭제
        for (ResumePortfolio existingPortfolio : existingPortfolios) {

            if (!portfolioIds.contains(existingPortfolio.getId())) {
                // 연결된 첨부파일 먼저 삭제
                resumeFileRepository.deleteAllByResumePortfolio_Id(existingPortfolio.getId());

                // 포트폴리오 삭제
                resumePortfolioRepository.delete(existingPortfolio);
            }
        }
    }
    // 이력서 삭제
    @Transactional
    @Override
    public void delete(Long resumeId){

        // 기존 이력서 조회
        Resume resume =
                resumeRepository.findById(resumeId)
                        .orElseThrow();
        // 경력 삭제
        resumeCareerRepository.deleteAllByResume_Id(
                resume.getId()
        );
        // 포트폴리오 첨부파일 삭제
        resumeFileRepository.deleteAllByResumePortfolio_Resume_Id(
                resume.getId()
        );
        // 포트폴리오 삭제
        resumePortfolioRepository.deleteAllByResume_Id(
                resume.getId()
        );
        // 프로필파일 삭제
        profileFileRepository.deleteByResume_Id(
                resume.getId()
        );
        // 이력서 삭제
        resumeRepository.delete(resume);
    }

    // 이력서 관리 페이지로 이동
    @Override
    public ResumeResponseDTO getResumeByMemberId(Long memberId) {
        Resume resume = resumeRepository
                .findByMemberId(memberId)
                .orElse(null);

        if (resume == null) {
            return null;
        }

        return getResume(resume.getId());
    }
}
