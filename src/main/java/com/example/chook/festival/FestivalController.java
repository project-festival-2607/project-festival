package com.example.chook.festival;

import com.example.chook.file.record.FileResource;
import com.example.chook.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Controller
@RequestMapping("/festival/*")
@Slf4j
@RequiredArgsConstructor
public class FestivalController {

    private final FestivalService festivalService;
    private final FileService fileService;

    // 프론트에서 JS로 API 받아올 때 필요한 키
    @Value("${apikey.festival}")
    private String APIKEY;

    @Value("${apikey.map}")
    private String mapApiKey;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(name = "pageNo", required = false, defaultValue = "1") int pageNo,
                       @RequestParam(name = "type", required = false) String type,
                       @RequestParam(name = "keyword", required = false) String keyword,
                       @RequestParam(name = "month", required = false) String month
                       ){

        Page<FestivalDTO> list = festivalService.getList(pageNo, type, keyword, month);
        PagingHandler fes = new PagingHandler(list, pageNo, type, keyword, month);

        model.addAttribute("fes", fes);

        return "/festival/list";
    }

    @GetMapping("/detail")
    public void detail(@RequestParam("id") String contentId, Model model){
        FestivalDTO festivalDTO = festivalService.getDetail(contentId);
        model.addAttribute("fes", festivalDTO);
        model.addAttribute("mapApiKey", mapApiKey);
    }

    @GetMapping("/register")
    public void register(){

    }

    @PostMapping("/register")
    public String register(FestivalDTO festivalDTO, @RequestParam(name = "imageFile", required = false)MultipartFile file){
        log.info(">>> register >>> {}", festivalDTO);
        festivalService.registerFes(festivalDTO, file);
        return "redirect:/festival/list";
    }

    @GetMapping("/remove")
    public String remove(@RequestParam("id") String id){
        festivalService.remove(id);
        return "redirect:/festival/list";
    }

    @GetMapping("/image/{uuid}")
    @ResponseBody
    public ResponseEntity<Resource> getImage(@PathVariable UUID uuid){
        FileResource file = fileService.getFile(uuid);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(file.mimeType()));
        return new ResponseEntity<>(
          file.resource(),
          headers,
          HttpStatus.OK
        );

    }

}
