package com.example.chook.support.controller;

import com.example.chook.support.dto.AdminBoardDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/adminBoard/*")
@Slf4j
public class AdminBoardController {

    @GetMapping("/list")
    public String list() {
        return "adminBoard/list";
    }

    @GetMapping("/register")
    public String register() {
        return "adminBoard/register";
    }

    // ponytail: 서비스 연결 전, 컨트롤러 도달 확인용 임시 echo 응답
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<AdminBoardDTO> register(@RequestBody AdminBoardDTO dto) {
        log.info("register received: {}", dto);
        return ResponseEntity.ok(dto);
    }

}
