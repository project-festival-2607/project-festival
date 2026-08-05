package com.example.chook.support.controller;

import com.example.chook.file.record.FileResource;
import com.example.chook.file.service.FileService;
import com.example.chook.support.dto.InquiryDTO;
import com.example.chook.support.entity.Inquiry;
import com.example.chook.support.service.InquiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/inquiry/*")
@RequiredArgsConstructor
@Slf4j
public class InquiryController {

    private final InquiryService inquiryService;
    private final FileService fileService;

    @GetMapping("/register")
    public String register() {
        return "inquiry/register";
    }

    @PostMapping("/register")
    public String register(
        InquiryDTO dto,
        @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments,
        RedirectAttributes redirectAttributes
    ) {
        Inquiry saved = inquiryService.register(dto, attachments);
        log.info("inquiry registered: {}", saved);
        redirectAttributes.addFlashAttribute("message", "문의가 접수되었습니다.");
        return "redirect:/inquiry/list";
    }

    @GetMapping("/list")
    public String list(Model model) {
        // ponytail: 로그인 미구현 - 지금은 전체 노출, 로그인 붙으면 본인 문의만 필터링
        model.addAttribute("inquiries", inquiryService.getList());
        return "inquiry/list";
    }

    @GetMapping("/detail/{ino}")
    public String detail(@PathVariable Long ino, Model model) {
        model.addAttribute("inquiry", inquiryService.getDetail(ino));
        return "inquiry/detail";
    }

    // ponytail: 로그인/관리자 페이지 구현 전 임시 답변 큐 - 실제 admin 관리 화면이 생기면 이관
    @GetMapping("/admin/list")
    public String adminList(
        @RequestParam(required = false) String searchType,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String answered,
        Model model
    ) {
        model.addAttribute("inquiries", inquiryService.getAdminList(searchType, keyword, answered));
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("answered", answered);
        return "inquiry/adminList";
    }

    @GetMapping("/admin/detail/{ino}")
    public String adminDetail(@PathVariable Long ino, Model model) {
        model.addAttribute("inquiry", inquiryService.getDetail(ino));
        return "inquiry/adminDetail";
    }

    @PostMapping("/admin/answer/{ino}")
    public String answer(@PathVariable Long ino, @RequestParam String comment) {
        Inquiry answered = inquiryService.answer(ino, comment);
        log.info("inquiry answered: {}", answered);
        return "redirect:/inquiry/admin/list";
    }

    @GetMapping("/file/{uuid}/download")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable UUID uuid) {
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