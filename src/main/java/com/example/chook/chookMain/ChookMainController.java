package com.example.chook.chookMain;

import com.example.chook.festival.FestivalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
@Slf4j
@RequiredArgsConstructor
public class ChookMainController {
    private final FestivalService festivalService;

    @GetMapping("/")
    public String chookMain(Model model){
        List<ChookMainDTO> mainList = festivalService.getMainList();

        if (!mainList.isEmpty()) {
            ChookMainDTO topFestival = mainList.get(0);

            List<ChookMainDTO> subList = mainList.subList(1, 5);

            model.addAttribute("topFestival", topFestival);
            model.addAttribute("subList", subList);
        }

        return "index";
    }
}
