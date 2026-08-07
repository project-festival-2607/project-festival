package com.example.chook.resume.controller;

import com.example.chook.member.repository.MemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import com.example.chook.resume.dto.ResumeRequestDTO;
import com.example.chook.resume.dto.ResumeResponseDTO;
import com.example.chook.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.example.chook.member.entity.Member;

@Controller
@RequestMapping("/resume")
@RequiredArgsConstructor
@Slf4j
public class ResumeController {

    private final ResumeService resumeService;
    private final MemberRepository memberRepository;

    // 이력서 작성 페이지 이동
    @GetMapping("/register")
    public String register(){

        return "resume/register";
    }

    // 이력서 등록
    @PostMapping("/register")
    public String register(
            @ModelAttribute ResumeRequestDTO resumeRequestDTO,
            // 현재 로그인한 사용자 정보
            Authentication authentication
    ){
        Member member = memberRepository.findByUsernameAndDeletedAtIsNull(
                authentication.getName()
        ).orElseThrow();

        resumeService.register(
                resumeRequestDTO,
                member.getId()
        );
        return "redirect:/resume/read";
    }

    // 이력서 조회
    @GetMapping("/read")
    public String read(
            @RequestParam Long resumeId,
            Model model
    ){
        ResumeResponseDTO resumeResponseDTO = resumeService.getResume(resumeId);

        model.addAttribute("resume", resumeResponseDTO);

        return "resume/read";
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
            @RequestParam Long resumeId
    ){
        resumeService.modify(
                resumeRequestDTO,
                resumeId
        );
        return "redirect:/resume/read?resumeId=" + resumeId;
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