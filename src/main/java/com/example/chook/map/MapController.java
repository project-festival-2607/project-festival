package com.example.chook.map;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/map/*")
@RequiredArgsConstructor
public class MapController {

    @Value("${apikey.map}")
    private String mapApiKey;

    @GetMapping("/map")
    public String mapPage(Model model) {

        model.addAttribute("mapApiKey", mapApiKey);

        return "map/map";
    }

}
