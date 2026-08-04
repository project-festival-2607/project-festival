package com.example.chook.support.controller;

import com.example.chook.file.dto.FileDTO;
import com.example.chook.file.record.FileResource;
import com.example.chook.file.service.FileService;
import com.example.chook.support.dto.AdminBoardDTO;
import com.example.chook.support.entity.AdminBoard;
import com.example.chook.support.service.AdminBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
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

import java.util.UUID;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/adminBoard/*")
@RequiredArgsConstructor
@Slf4j
public class AdminBoardController {

    // ponytail: 폴더명으로 못 쓰는 문자만 제거, OS별 세밀한 검증은 필요해지면 추가
    private static final Pattern INVALID_FOLDER_CHARS = Pattern.compile("[\\\\/:*?\"<>|]");

    private final FileService fileService;
    private final AdminBoardService adminBoardService;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "1") int page, Model model) {
        Page<AdminBoardDTO> boardPage = adminBoardService.getList(page);
        model.addAttribute("boardPage", boardPage);
        return "adminBoard/list";
    }

    @GetMapping("/register")
    public String register() {
        return "adminBoard/register";
    }

    @PostMapping("/register")
    public String register(AdminBoardDTO dto) {
        AdminBoard saved = adminBoardService.register(dto);
        log.info("admin board saved: {}", saved);
        return "redirect:/adminBoard/list";
    }

    // 에디터에 이미지를 삽입하는 시점에 비동기로 호출됨 (addImageBlobHook)
    @PostMapping("/uploadImage")
    @ResponseBody
    public ResponseEntity<FileDTO> uploadImage(
        @RequestParam("image") MultipartFile image,
        @RequestParam("title") String title
    ) {
        String relativePath = toFolderName(title); // 파일 저장 경로
        FileDTO fileDto = fileService.uploadAndGetDto(image, relativePath);
        log.info("adminBoard image uploaded: {}", fileDto);
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
