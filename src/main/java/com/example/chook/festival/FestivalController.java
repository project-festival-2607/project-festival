package com.example.chook.festival;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/festival/*")
@Slf4j
@RequiredArgsConstructor
public class FestivalController {
    private final FestivalService festivalService;

    @Value("${apikey.festival}")
    private String APIKEY;

    @GetMapping("/list")
    public String list(Model model){
        model.addAttribute("apikey", APIKEY);
        return "/festival/list";
    }
}
