package com.example.chook.resume.controller;

import com.example.chook.file.entity.UploadedFile;
import com.example.chook.file.record.FileResource;
import com.example.chook.file.service.FileService;
import com.example.chook.member.repository.MemberRepository;
import com.example.chook.member.service.MemberService;
import com.example.chook.resume.dto.ResumeFileDTO;
import com.example.chook.resume.dto.ResumePortfolioDTO;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Controller
@RequestMapping("/resume")
@RequiredArgsConstructor
@Slf4j
public class ResumeController {

    private final ResumeService resumeService;
    private final MemberRepository memberRepository;
    private final FileService fileService;
    private final MemberService memberService;

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

    // 이력서 삭제 전 비밀번호 확인 페이지
    @GetMapping("/delete-password")
    public String deletePassword(
            @RequestParam Long resumeId,
            @AuthenticationPrincipal UserDetails user,
            Model model
    ) {
        if (user == null) {
            return "redirect:/member/login";
        }
        model.addAttribute("resumeId", resumeId);
        model.addAttribute("username", user.getUsername());

        return "resume/delete-password";
    }

    // 이력서 삭제 전 비밀번호 확인
    @PostMapping("/delete-password")
    public String deletePassword(
            @RequestParam Long resumeId,
            @RequestParam String password,
            @AuthenticationPrincipal UserDetails user,
            Model model
    ) {
        if (user == null) {
            return "redirect:/member/login";
        }
        Member member = memberRepository
                .findByUsernameAndDeletedAtIsNull(user.getUsername())
                .orElseThrow();

        // 비밀번호 확인
        if (!memberService.verifyPassword(member.getId(), password)) {
            model.addAttribute("resumeId", resumeId);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("FailureMsg", "비밀번호가 일치하지 않습니다.");
            return "resume/delete-password";
        }

        // 비밀번호가 맞으면 실제 이력서 삭제
        resumeService.delete(resumeId);
        return "redirect:/resume/manage";
    }

    // 이력서 파일 조회 (프로필/첨부파일)
    @GetMapping("/file/{uuid}")
    public ResponseEntity<Resource> getResumeFile(@PathVariable UUID uuid){
        FileResource file = fileService.getFile(uuid);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.mimeType()))
                .body(file.resource());
    }

    // 이력서 파일 다운로드 (프로필/첨부파일)
    @GetMapping("/file/{uuid}/download")
    public ResponseEntity<Resource> downloadResumeFile(@PathVariable UUID uuid){
        FileResource file = fileService.getFile(uuid);

        ContentDisposition contentDisposition = ContentDisposition.attachment()
                        .filename(file.originalName(), StandardCharsets.UTF_8)
                        .build();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.mimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(file.resource());
    }
}