package com.example.chook.resume.service;

import com.example.chook.file.entity.UploadedFile;
import com.example.chook.file.repository.UploadedFileRepository;
import com.example.chook.member.entity.Member;
import com.example.chook.member.repository.MemberRepository;
import com.example.chook.resume.dto.*;
import com.example.chook.resume.entity.*;
import com.example.chook.resume.repository.*;
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
        Resume resume =
                resumeRepository.findById(resumeId)
                        .orElseThrow();

        // 기존 자기소개서 수정
        resume.setIntroduction(
                resumeRequestDTO.getIntroduction()
        );

        resumeRepository.save(resume);

        // 기존 경력사항 수정
        if(resumeRequestDTO.getCareers() != null){
            for(ResumeCareerDTO careerDTO : resumeRequestDTO.getCareers()) {
                ResumeCareer resumeCareer =
                        resumeCareerRepository.findById(
                                        careerDTO.getId()
                                )
                                .orElseThrow();
                resumeCareer.setCareerName(careerDTO.getCareerName());

                resumeCareer.setStartDate(careerDTO.getStartDate());

                resumeCareer.setEndDate(careerDTO.getEndDate());

                resumeCareer.setDuties(careerDTO.getDuties());

                resumeCareerRepository.save(resumeCareer);
            }
        }

        // 포트폴리오 수정
        if(resumeRequestDTO.getPortfolios() != null){

            for(ResumePortfolioDTO portfolioDTO : resumeRequestDTO.getPortfolios()) {
                ResumePortfolio  resumePortfolio =
                        resumePortfolioRepository.findById(
                                        portfolioDTO.getId()
                                )
                                .orElseThrow();
                resumePortfolio.setTitle(portfolioDTO.getTitle());

                resumePortfolio.setType(portfolioDTO.getType());

                resumePortfolioRepository.save(resumePortfolio);

                // 포트폴리오 첨부파일 수정
                if (portfolioDTO.getResumeFile() != null){

                    // 새 파일 조회
                    UploadedFile uploadedFile =
                            uploadedFileRepository.findById(
                                    portfolioDTO.getResumeFile().getUuid()
                                    )
                                    .orElseThrow();
                    // 기존 첨부파일 조회
                    ResumeFile resumeFile =
                            resumeFileRepository
                                    .findByResumePortfolio_Id(resumePortfolio.getId()
                                    )
                                    .orElse(null);
                    // 기존 파일이 있으면 변경
                    if(resumeFile != null){

                        resumeFile.setUploadedFile(uploadedFile);

                        resumeFileRepository.save(resumeFile);

                    }else {
                        // 파일이 없었다면 새로 생성
                        ResumeFile newResumeFile =
                                ResumeFile.builder()
                                        .resumePortfolio(resumePortfolio)
                                        .uploadedFile(uploadedFile)
                                        .build();
                        resumeFileRepository.save(newResumeFile);
                    }
                }
            }
        }
    }

    // 이력서 삭제
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
}
