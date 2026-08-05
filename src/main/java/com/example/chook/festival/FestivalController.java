package com.example.chook.festival;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/festival/*")
@Slf4j
@RequiredArgsConstructor
public class FestivalController {
    private final FestivalService festivalService;

    // 프론트에서 JS로 API 받아올 때 필요한 키
    @Value("${apikey.festival}")
    private String APIKEY;

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNo,
                       @RequestParam(name = "type", required = false) String type,
                       @RequestParam(name = "keyword", required = false) String keyword
                       ){
        // 프론트에서 API 요청하기 위해 심는 키 (백에서 요청하면 필요 X) 우선은 TEST로 놔둠
        model.addAttribute("apikey", APIKEY);

        Page<FestivalDTO> list = festivalService.getList(pageNo, type, keyword);
        PagingHandler fes = new PagingHandler(list, pageNo, type, keyword);

        model.addAttribute("fes", fes);

        return "/festival/list";
    }

    @GetMapping("/detail")
    public void detail(@RequestParam("id") String contentId, Model model){
        FestivalDTO festivalDTO = festivalService.getDetail(contentId);
        model.addAttribute("fes", festivalDTO);
    }
}
