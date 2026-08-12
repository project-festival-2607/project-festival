package com.example.chook.application.controller;

import com.example.chook.application.dto.ApplicationDTO;
import com.example.chook.application.dto.ApplyDTO;
import com.example.chook.application.entity.enums.ApplicationResult;
import com.example.chook.application.service.ApplicationService;
import com.example.chook.member.security.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/application/*")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

    private final ApplicationService applicationService;

    // 지원하기 기능
    @PostMapping("/apply")
    public String apply (ApplicationDTO applicationDTO){
        applicationService.apply(applicationDTO);

        return "redirect:/";
    }

    // 내가 지원한 목록 조회
    @GetMapping("/list")
    public String list(@RequestParam Long memberId, Model model) {

        model.addAttribute(
                "applicationList",
                applicationService.getList(memberId)
        );
        return "application/list";
    }

    // 지원 상세 조회
    @GetMapping("/detail")
    public String detail (@RequestParam Long id, Model model){

        ApplicationDTO applicationDTO = applicationService.getDetail(id);

        model.addAttribute(
                "application",
                applicationDTO
        );
        return "application/detail";
    }

    // 지원 취소
    @PostMapping("/cancel")
    public String cancel (@RequestParam Long id){

        applicationService.cancel(id);

        return "redirect:/application/list";
    }

    // 합격/불합격 처리
    @PostMapping("/result")
    public String updateResult(@RequestParam Long id, @RequestParam ApplicationResult result){

        applicationService.updateResult(id, result);

        return "redirect:/application/list";
    }

    // 지원서 열람 처리
    @PostMapping("/read")
    public String read(@RequestParam Long id){

        applicationService.read(id);

        return "redirect:/application/detail?id=" + id;
    }


    // applypage Zone
    @GetMapping("/apply")
    public String applyPage(
            @RequestParam Long recruitmentId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        // 로그인하지 않은 경우
        if (userDetails == null) {

            // 로그인 후 돌아갈 주소 저장
            session.setAttribute(
                    "applicationRedirectAfterLogin",
                    "/application/apply?recruitmentId=" + recruitmentId
            );

            return "redirect:/member/login";
        }

        // 로그인한 회원의 지원 정보 조회
        ApplyDTO applyDTO =
                applicationService.getApplyData(
                        recruitmentId,
                        userDetails.getId()
                );

        // 이력서가 없는 경우
        if (applyDTO.getResume() == null) {

            redirectAttributes.addFlashAttribute(
                    "resumeMessage",
                    "지원하려면 먼저 이력서를 작성해주세요."
            );

            return "redirect:/recruitment/" + recruitmentId;
        }

        // 이력서가 있는 경우
        model.addAttribute("apply", applyDTO);

        return "application/apply";
    }

}
