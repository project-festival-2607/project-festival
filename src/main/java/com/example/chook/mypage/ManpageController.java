package com.example.chook.mypage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ManpageController {

    @GetMapping("/mypage")
    public String mypage() {
        return "payment/starting";
    }
}