package com.example.chook.application.controller;

import com.example.chook.application.dto.ApplicationCategoryListDTO;
import com.example.chook.application.dto.ApplicationDTO;
import com.example.chook.application.dto.ApplyDTO;
import com.example.chook.application.entity.enums.ApplicationResult;
import com.example.chook.application.service.ApplicationService;
import com.example.chook.file.record.FileResource;
import com.example.chook.file.service.FileService;
import com.example.chook.member.entity.Member;
import com.example.chook.member.entity.enums.MemberRole;
import com.example.chook.member.repository.MemberRepository;
import com.example.chook.member.security.CustomUserDetails;
import com.example.chook.recruitment.entity.enums.RecruitmentCategory;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/application/*")
@RequiredArgsConstructor
@Slf4j
public class ApplicationController {

    private final ApplicationService applicationService;
    private final MemberRepository memberRepository;
    private final FileService fileService;

    // 지원하기 기능
    @PostMapping("/apply")
    public String apply (ApplicationDTO applicationDTO){
        applicationService.apply(applicationDTO);

        return "redirect:/application/jobseeker/list";
    }

    // 내가 지원한 목록 조회 (구직자용)
    @GetMapping("/jobseeker/list")
    public String list(@AuthenticationPrincipal UserDetails user, Model model) {
        // 로그인하지 않은 경우
        if (user == null) {
            return "redirect:/member/login";
        }

        // 현재 로그인한 회원의 username
        String username = user.getUsername();

        // DB에서 회원 조회
        Member member = memberRepository
                .findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow();

        // 현재 회원의 지원 목록 조회
        model.addAttribute(
                "applicationList",
                applicationService.getList(member.getId())
        );
        return "application/jobseeker/list";
    }

    // 특정 모집공고에 지원한 구직자 목록 조회 (구인자용)
    @GetMapping("/recruiter/list")
    public String applicants(@AuthenticationPrincipal UserDetails user, Model model) {

        // 로그인하지 않은 경우
        if (user == null) {
            return "redirect:/member/login";
        }

        // 현재 로그인한 구인자의 username
        String username = user.getUsername();

        // 구인자의 모집공고 지원자를 카테고리별로 조회
        ApplicationCategoryListDTO applicationList = applicationService.getApplicantsByRecruiter(username);

        // 카테고리별 지원자 목록 전달
        model.addAttribute(
                "individualApplications",
                applicationList.getIndividualApplications()
        );

        model.addAttribute(
                "foodTruckApplications",
                applicationList.getFoodTruckApplications()
        );

        model.addAttribute(
                "equipmentApplications",
                applicationList.getEquipmentApplications()
        );

        model.addAttribute(
                "etcApplications",
                applicationList.getEtcApplications()
        );
        return "application/recruiter/list";
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

        return "redirect:/application/jobseeker/list";
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

        // 이미 지원한 공고일 경우
        if (applicationService.existsByMemberIdAndRecruitmentId(
                userDetails.getId(),
                recruitmentId
        )) {
            redirectAttributes.addFlashAttribute(
                    "applicationMessage",
                    "이미 지원한 공고입니다."
            );

            return "redirect:/recruitment/" + recruitmentId;
        }

        // 푸드트럭 지원 제한
        if (applyDTO.getRecruitment().getCategory() == RecruitmentCategory.FOOD_TRUCK
                && userDetails.getRole() == MemberRole.JOB_SEEKER) {

            redirectAttributes.addFlashAttribute(
                    "applicationMessage",
                    "푸드트럭 공고는 사업자등록번호가 등록된 구직자만 지원할 수 있습니다."
            );

            return "redirect:/recruitment/" + recruitmentId;
        }

        // 이력서가 없는 경우
        if (applyDTO.getResume() == null) {

            if(userDetails.getRole() == MemberRole.JOB_SEEKER ||
               userDetails.getRole() == MemberRole.JOB_EQUIP){
                redirectAttributes.addFlashAttribute(
                        "resumeMessage",
                        "지원하려면 먼저 이력서를 작성해주세요."
                );
            }

            if(userDetails.getRole() == MemberRole.RECRUITER){
                redirectAttributes.addFlashAttribute(
                        "resumeMessage",
                        "구직자가 이용 가능한 서비스입니다."
                );
            }


            return "redirect:/recruitment/" + recruitmentId;
        }

        // 이력서가 있는 경우
        model.addAttribute("apply", applyDTO);

        return "application/apply";
    }

    @GetMapping("/image/{uuid}")
    public ResponseEntity<Resource> getImage(@PathVariable UUID uuid) {
        FileResource file = fileService.getFile(uuid);
        return ResponseEntity.ok()
          .contentType(MediaType.parseMediaType(file.mimeType()))
          .body(file.resource());
    }

}
