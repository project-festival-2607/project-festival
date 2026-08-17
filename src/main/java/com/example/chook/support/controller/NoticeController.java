package com.example.chook.support.controller;

import com.example.chook.file.dto.FileDTO;
import com.example.chook.file.record.FileResource;
import com.example.chook.file.service.FileService;
import com.example.chook.member.entity.enums.MemberRole;
import com.example.chook.member.security.CustomUserDetails;
import com.example.chook.support.dto.AdminBoardDTO;
import com.example.chook.support.entity.Notice;
import com.example.chook.support.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/notice/*")
@RequiredArgsConstructor
@Slf4j
public class NoticeController {

    // ponytail: 폴더명으로 못 쓰는 문자만 제거, OS별 세밀한 검증은 필요해지면 추가
    private static final Pattern INVALID_FOLDER_CHARS = Pattern.compile("[\\\\/:*?\"<>|]");

    private final FileService fileService;
    private final NoticeService noticeService;

    @GetMapping("/list")
    public String list(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(required = false) String searchType,
        @RequestParam(required = false) String keyword,
        @AuthenticationPrincipal CustomUserDetails user,
        Model model
    ) {
        Page<AdminBoardDTO> boardPage = noticeService.getList(page, searchType, keyword);
        model.addAttribute("boardPage", boardPage);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        model.addAttribute("isAdmin", isAdmin(user));
        return "notice/list";
    }

    @GetMapping("/register")
    public String register(@AuthenticationPrincipal CustomUserDetails user) {
        requireAdmin(user);
        return "notice/register";
    }

    @GetMapping("/detail/{bno}")
    public String detail(
        @PathVariable Long bno,
        @AuthenticationPrincipal CustomUserDetails user,
        Model model
    ) {
        model.addAttribute("board", noticeService.getDetail(bno));
        model.addAttribute("isAdmin", isAdmin(user));
        return "notice/detail";
    }

    @PostMapping("/register")
    public String register(@AuthenticationPrincipal CustomUserDetails user, AdminBoardDTO dto) {
        requireAdmin(user);
        Notice saved = noticeService.register(dto);
        log.info("admin board saved: {}", saved);
        return "redirect:/notice/list";
    }

    @PostMapping("/modify/{bno}")
    public String modify(
        @PathVariable Long bno,
        @AuthenticationPrincipal CustomUserDetails user,
        AdminBoardDTO dto
    ) {
        requireAdmin(user);
        Notice modified = noticeService.modify(bno, dto);
        log.info("admin board modified: {}", modified);
        return "redirect:/notice/detail/" + bno;
    }

    @PostMapping("/delete/{bno}")
    public String delete(
        @PathVariable Long bno,
        @AuthenticationPrincipal CustomUserDetails user
    ) {
        requireAdmin(user);
        noticeService.delete(bno);
        log.info("admin board deleted: {}", bno);
        return "redirect:/notice/list";
    }

    private boolean isAdmin(CustomUserDetails user) {
        return user != null && user.getRole() == MemberRole.ADMIN;
    }

    private void requireAdmin(CustomUserDetails user) {
        if (!isAdmin(user)) {
            throw new AccessDeniedException("관리자만 접근할 수 있음");
        }
    }

    // 에디터에 이미지를 삽입하는 시점에 비동기로 호출됨 (addImageBlobHook)
    @PostMapping("/uploadImage")
    @ResponseBody
    public ResponseEntity<FileDTO> uploadImage(
        @RequestParam("image") MultipartFile image,
        @RequestParam("title") String title
    ) {
        String relativePath = "notice/" + toFolderName(title); // 파일 저장 경로
        FileDTO fileDto = fileService.toDto(fileService.upload(image, relativePath));
        log.info("notice image uploaded: {}", fileDto);
        return ResponseEntity.ok(fileDto);
    }

    @GetMapping("/image/{uuid}")
    @ResponseBody
    public ResponseEntity<Resource> getImage(@PathVariable UUID uuid) {
        FileResource file = fileService.getFile(uuid);
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(file.mimeType()))
            .body(file.resource());
    }

    private String toFolderName(String title) {
        String trimmed = title == null ? "" : title.trim();
        return trimmed.isEmpty()
            ? "untitled"
            : INVALID_FOLDER_CHARS.matcher(trimmed).replaceAll("_");
    }

}
