package com.example.chook.chookMain;

import com.example.chook.festival.FestivalService;
import com.example.chook.recruitment.service.RecruitmentService;
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
    private final RecruitmentService recruitmentService;

    @GetMapping("/")
    public String chookMain(Model model){
        List<ChookMainDTO> mainList = festivalService.getMainList();
        List<ChookRecruitmentDTO> recruitmentList = recruitmentService.getMainList();

        if (!mainList.isEmpty()) {
            ChookMainDTO topFestival = mainList.get(1);

            List<ChookMainDTO> subList = mainList.subList(2, 10);

            model.addAttribute("topFestival", topFestival);
            model.addAttribute("subList", subList);
        }

        if(!recruitmentList.isEmpty()){
            List<ChookRecruitmentDTO> recList = recruitmentList.subList(0, 8);
            model.addAttribute("recList", recList);
        }


        return "index";
    }
}
