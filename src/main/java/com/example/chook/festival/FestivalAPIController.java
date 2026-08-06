package com.example.chook.festival;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/list")
    public ResponseEntity<Page<FestivalDTO>> getFestivalList(
            @RequestParam(name = "page", defaultValue = "1") int pageNo,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "month", required = false) String month
    ){
        Page<FestivalDTO> festival = festivalService.getList(pageNo, type, keyword, month);
        return ResponseEntity.ok(festival);
    }

    // map Zone
    @GetMapping("/map")
    public ResponseEntity<List<FestivalDTO>> getMapFestivalList() {
        return ResponseEntity.ok(festivalService.getAll());
    }

}
