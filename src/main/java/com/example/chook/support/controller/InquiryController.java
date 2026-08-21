package com.example.chook.support.controller;

import com.example.chook.file.record.FileResource;
import com.example.chook.file.service.FileService;
import com.example.chook.member.entity.enums.MemberRole;
import com.example.chook.member.security.CustomUserDetails;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public String register(@AuthenticationPrincipal CustomUserDetails user, RedirectAttributes redirectAttributes) {
        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }
        return "inquiry/register";
    }

    @PostMapping("/register")
    public String register(
        InquiryDTO dto,
        @RequestParam(value = "attachments", required = false) List<MultipartFile> attachments,
        @AuthenticationPrincipal CustomUserDetails user,
        RedirectAttributes redirectAttributes
    ) {
        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }
        dto.setId(user.getId());
        Inquiry saved = inquiryService.register(dto, attachments);
        log.info("inquiry registered: {}", saved);
        redirectAttributes.addFlashAttribute("message", "문의가 접수되었습니다.");
        return "redirect:/inquiry/list";
    }

    @GetMapping("/list")
    public String list(@AuthenticationPrincipal CustomUserDetails user, Model model, RedirectAttributes redirectAttributes) {
        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "로그인이 필요합니다.");
            return "redirect:/member/login";
        }
        model.addAttribute("inquiries", inquiryService.getList(user.getId()));
        return "inquiry/list";
    }

    @GetMapping("/detail/{ino}")
    public String detail(@PathVariable Long ino, @AuthenticationPrincipal CustomUserDetails user, Model model) {
        InquiryDTO inquiry = inquiryService.getDetail(ino);
        boolean isAdmin = user != null && user.getRole() == MemberRole.ADMIN;
        if (!isAdmin && (user == null || !user.getId().equals(inquiry.getId()))) {
            throw new AccessDeniedException("본인 문의만 조회할 수 있음");
        }
        model.addAttribute("inquiry", inquiry);
        return "inquiry/detail";
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