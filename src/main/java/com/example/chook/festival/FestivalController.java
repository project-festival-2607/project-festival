package com.example.chook.festival;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/festival/*")
@Slf4j
@RequiredArgsConstructor
public class FestivalController {
    private final FestivalService festivalService;
    private final FesImageHandler fesImageHandler;

    // 프론트에서 JS로 API 받아올 때 필요한 키
    @Value("${apikey.festival}")
    private String APIKEY;

    @Value("${file.upload-dir}")
    private String uploadDir;

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

    @GetMapping("/register")
    public void register(){

    }

    @PostMapping("/register")
    public String register(FestivalDTO festivalDTO, @RequestParam(name = "imageFile", required = false)MultipartFile file){
        log.info(">>> register >>> {}", festivalDTO);

        FesImageDTO fesImage = null;

        if(file != null) {
            fesImage = fesImageHandler.uploadFile(file);
        }

        festivalService.registerFes(festivalDTO, fesImage);

        return "redirect:/festival/list";
    }

    @GetMapping("/remove")
    public String remove(@RequestParam("id") String id){
        festivalService.remove(id);
        return "redirect:/festival/list";
    }

    @GetMapping("/display")
    @ResponseBody
    public ResponseEntity<Resource> displayImage(@RequestParam("fileName") String fileName){
        File file = new File(uploadDir + "/festival", fileName);

        if(!file.exists()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Resource resource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();

        try{
            Path path = Paths.get(file.getAbsolutePath());
            headers.add("Content-Type", Files.probeContentType(path));
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);

    }

}
