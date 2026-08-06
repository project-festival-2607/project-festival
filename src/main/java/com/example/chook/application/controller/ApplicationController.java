package com.example.chook.application.controller;

import com.example.chook.application.dto.ApplicationDTO;
import com.example.chook.application.entity.enums.ApplicationResult;
import com.example.chook.application.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/application")
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
}
