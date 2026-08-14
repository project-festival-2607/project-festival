package com.example.chook.resume.controller;

import com.example.chook.file.entity.UploadedFile;
import com.example.chook.file.service.FileService;
import com.example.chook.member.repository.MemberRepository;
import com.example.chook.resume.dto.ResumeFileDTO;
import com.example.chook.resume.dto.ResumePortfolioDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import com.example.chook.resume.dto.ResumeRequestDTO;
import com.example.chook.resume.dto.ResumeResponseDTO;
import com.example.chook.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.chook.member.entity.Member;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/resume")
@RequiredArgsConstructor
@Slf4j
public class ResumeController {

    private final ResumeService resumeService;
    private final MemberRepository memberRepository;
    private final FileService fileService;

    // 이력서 관리 페이지로 이동
    @GetMapping("/manage")
    public String manage(@AuthenticationPrincipal UserDetails user, Model model) {

        // 로그인하지 않은 경우
        if (user == null) {
            return "redirect:/member/login";
        }

        Member member = memberRepository
                .findByUsernameAndDeletedAtIsNull(
                        user.getUsername()
                )
                .orElseThrow();

        ResumeResponseDTO resumeResponseDTO =
                resumeService.getResumeByMemberId(member.getId());

        model.addAttribute("resume", resumeResponseDTO);

        return "resume/manage";
    }

    // 이력서 작성 페이지 이동
    @GetMapping("/register")
    public String register(){

        return "resume/register";
    }

    // 이력서 등록
    @PostMapping("/register")
    public String register(
            @ModelAttribute ResumeRequestDTO resumeRequestDTO,
            @RequestParam(name = "profileImage", required = false)
            MultipartFile profileImage,
            Authentication authentication
    ) {

        Member member = memberRepository.findByUsernameAndDeletedAtIsNull(
                        authentication.getName()
                )
                .orElseThrow();

        // 프로필 사진이 있으면 파일 업로드
        if (profileImage != null && !profileImage.isEmpty()) {

            UploadedFile uploadedFile = fileService.upload(profileImage, "resume/profile");

            // 업로드된 파일 UUID를 DTO에 저장
            resumeRequestDTO.setProfileFileUuid(
                    uploadedFile.getUuid().toString()
            );
        }

        // 포트폴리오 파일 업로드
        if (resumeRequestDTO.getPortfolios() != null) {

            for (ResumePortfolioDTO portfolioDTO : resumeRequestDTO.getPortfolios()) {

                MultipartFile portfolioFile = portfolioDTO.getFile();

                if (portfolioFile != null && !portfolioFile.isEmpty()) {

                    UploadedFile uploadedFile = fileService.upload(
                                    portfolioFile,
                                    "resume/portfolio"
                            );

                    ResumeFileDTO resumeFileDTO = ResumeFileDTO.builder()
                                    .uuid(uploadedFile.getUuid())
                                    .originalName(
                                            uploadedFile.getOriginalName()
                                    )
                                    .build();

                    portfolioDTO.setResumeFile(resumeFileDTO);

                }
            }
        }
        // 이력서 저장
        resumeService.register(resumeRequestDTO, member.getId());

        return "redirect:/resume/manage";
    }

    // 이력서 수정 페이지 이동
    @GetMapping("/modify")
    public String modify(
            @RequestParam Long resumeId,
            Model model
    ){
        ResumeResponseDTO resumeResponseDTO =
                resumeService.getResume(resumeId);
        model.addAttribute(
                "resume",
                resumeResponseDTO
        );
        return "resume/modify";
    }

    // 이력서 수정
    @PostMapping("/modify")
    public String modify(
            @ModelAttribute ResumeRequestDTO resumeRequestDTO,
            @RequestParam Long resumeId,
            @RequestParam(name = "profileImage", required = false)
            MultipartFile profileImage
    ){
        // 프로필 사진 수정
        if (profileImage != null && !profileImage.isEmpty()){

            UploadedFile uploadedFile = fileService.upload(profileImage, "resume/profile");

            resumeRequestDTO.setProfileFileUuid(uploadedFile.getUuid().toString());
        }

        // 포트폴리오 첨부파일 수정
        if (resumeRequestDTO.getPortfolios() != null){

            for (ResumePortfolioDTO portfolioDTO : resumeRequestDTO.getPortfolios()){

                MultipartFile portfolioFile = portfolioDTO.getFile();

                if (portfolioFile != null && !portfolioFile.isEmpty()){

                    UploadedFile uploadedFile = fileService.upload(
                                    portfolioFile,
                                    "resume/portfolio"
                            );

                    ResumeFileDTO resumeFileDTO = ResumeFileDTO.builder()
                                    .uuid(uploadedFile.getUuid())
                                    .originalName(
                                            uploadedFile.getOriginalName()
                                    )
                                    .build();
                    portfolioDTO.setResumeFile(resumeFileDTO);
                }
            }
        }
        resumeService.modify(resumeRequestDTO, resumeId);

        return "redirect:/resume/manage";
    };

    // 지원자 이력서 상세 조회 (구인자용)
    @GetMapping("/detail")
    public String detail(@RequestParam Long id, Model model) {

        // 이력서 조회
        ResumeResponseDTO resumeResponseDTO = resumeService.getResume(id);

        // 이력서 정보 전달
        model.addAttribute(
                "resume",
                resumeResponseDTO
        );

        return "resume/detail";
    }

    // 이력서 삭제
    @PostMapping("/delete")
    public String delete(
            @RequestParam Long resumeId
    ){
        resumeService.delete(resumeId);

        return "redirect:/";
    }
}