package com.example.chook.support.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

}
