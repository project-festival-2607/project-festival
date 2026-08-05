package com.example.chook.festival;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/festival/*")
public class FestivalAPIController {

    private final FestivalService festivalService;

    // API 요청 백엔드로 넘기기 전 프론트엔드에서 처리한 코드

//    @PostMapping("/save")
//    public ResponseEntity<String> save(@RequestBody FestivalDTO festivalDTO){
//        String contentId = festivalService.save(festivalDTO);
//
//        return contentId != null ? new ResponseEntity<String>("OK", HttpStatus.OK) : new ResponseEntity<String>("NO", HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    @PostMapping("/saveAll")
//    public ResponseEntity<String> saveAll(@RequestBody List<FestivalDTO> festivalDTOList){
//        try{
//            festivalService.saveAll(festivalDTOList);
//            return new ResponseEntity<>("OK", HttpStatus.OK);
//        }catch (Exception e){
//            log.info(">>> ERROR >> {}", e);
//            return new ResponseEntity<>("NO", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    // 1월 2월 ... 비동기 카테고리


}
