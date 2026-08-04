package com.example.chook.festival;

import com.example.chook.entity.Festival;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
//public class FestivalServiceImpl implements FestivalService, ApplicationRunner
public class FestivalServiceImpl implements FestivalService, ApplicationRunner {
    private final FestivalRepository festivalRepository;

    @Value("${apikey.festival}")
    private String apiKey;
    @Override
    public String save(FestivalDTO festivalDTO) {
        Festival festival = convertDTOToEntity(festivalDTO);
        Festival savedFes = festivalRepository.save(festival);
        String contentId = savedFes.getContentId();

        return contentId;
    }

    @Override
    @Transactional
    public void saveAll(List<FestivalDTO> festivalDTOList) {
        List<Festival> festivalList = festivalDTOList.stream()
                .map(this::convertDTOToEntity)
                .collect(Collectors.toList());

        festivalRepository.saveAll(festivalList);
    }

    // 페이징 리스트
    @Override
    public Page<FestivalDTO> getList(int pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 18, Sort.by("startDate").descending());
        Page<Festival> pageList = festivalRepository.findAll(pageable);
        return pageList.map(this::convertEntityToDTO);
    }

    @Override
    public FestivalDTO getDetail(String contentId) {
        Festival festival = festivalRepository.findById(contentId).orElseThrow(() -> new EntityNotFoundException("해당 축제가 없습니다."));
        FestivalDTO festivalDTO = convertEntityToDTO(festival);
        return festivalDTO;
    }

//    DB에서 API 요청 후 백그라운드에서 동기화

    @Override
    @Transactional
    public void run(@NonNull ApplicationArguments args) throws Exception{
        if(festivalRepository.count() > 0){
            log.info("DB 데이터 동기 완료");
            return;
            // 나중에 새로 갱신될 때를 대비하여 ID로 비교하는 로직으로 바꿀 것! ===> 지금은 TEST
        }

        log.info("Festival DB 데이터 동기화 시작...");

        try {
            String url = "https://apis.data.go.kr/B551011/KorService2/searchFestival2?numOfRows=100&MobileOS=WEB&MobileApp=CHUCK&_type=json&arrange=R&eventStartDate=20260101&serviceKey=" + apiKey;

            RestTemplate restTemplate = new RestTemplate(); // 백엔드에서 RestAPI 실행시켜주는 객체
            String listResponse = restTemplate.getForObject(url, String.class);

            ObjectMapper mapper = new ObjectMapper(); // JsonNode 포장 꺼내주는 객체
            JsonNode rootNode = mapper.readTree(listResponse);
            JsonNode items = rootNode.path("response").path("body").path("items").path("item");

            if(!items.isArray() || items.isEmpty()){
                log.info("축제 데이터 존재하지 않음.");
                return;
            }

            List<FestivalDTO> festivalDTOList = new ArrayList<>();

            for(JsonNode item : items){
                String contentId = item.path("contentid").asText(null);

                String commonUrl = "https://apis.data.go.kr/B551011/KorService2/detailCommon2?MobileOS=WEB&MobileApp=CHUCK&_type=json&contentId=" + contentId + "&serviceKey=" + apiKey;
                String introUrl = "https://apis.data.go.kr/B551011/KorService2/detailIntro2?MobileOS=WEB&MobileApp=CHUCK&_type=json&contentId=" + contentId + "&contentTypeId=15&serviceKey=" + apiKey;

                try {
                    String commonRes = restTemplate.getForObject(commonUrl, String.class);
                    String introRes = restTemplate.getForObject(introUrl, String.class);

                    JsonNode commonItem = mapper.readTree(commonRes).path("response").path("body").path("items").path("item");
                    JsonNode introItem = mapper.readTree(introRes).path("response").path("body").path("items").path("item");

                    if(commonItem.isArray() && !commonItem.isEmpty()) {
                        commonItem = commonItem.get(0);
                    }
                    if(introItem.isArray() && !introItem.isEmpty()) {
                        introItem = introItem.get(0);
                    }

                    FestivalDTO festivalDTO = FestivalDTO.builder()
                            .contentId(contentId)
                            .title(commonItem.path("title").asText())
                            .homepage(commonItem.path("homepage").isNull() ? null : commonItem.path("homepage").asText())
                            .mapX(commonItem.path("mapx").isNull() ? null : commonItem.path("mapx").asDecimal())
                            .mapY(commonItem.path("mapy").isNull() ? null : commonItem.path("mapy").asDecimal())
                            .overview(commonItem.path("overview").isNull() ? null : commonItem.path("overview").asText())
                            .tel(commonItem.path("tel").isNull() ? null : commonItem.path("tel").asText())
                            .telName(commonItem.path("telname").isNull() ? null : commonItem.path("telname").asText())
                            .ageLimit(introItem.path("agelimit").isNull() ? null : introItem.path("agelimit").asText())
                            .eventPlace(introItem.path("eventplace").isNull() ? null : introItem.path("eventplace").asText())
                            .zipCode(commonItem.path("zipcode").isNull() ? null : commonItem.path("zipcode").asText())
                            .playTime(introItem.path("playtime").isNull() ? null : introItem.path("playtime").asText())
                            .program(introItem.path("program").isNull() ? null : introItem.path("program").asText())
                            .useTime(introItem.path("usetimefestival").isNull() ? null : introItem.path("usetimefestival").asText())
                            .startDate(introItem.path("eventstartdate").isNull() ? introItem.path("eventstartdate").asText(null) : introItem.path("eventstartdate").asText())
                            .endDate(introItem.path("eventenddate").isNull() ? null : introItem.path("eventenddate").asText())
                            .firstImage(commonItem.path("firstimage").isNull() ? null : commonItem.path("firstimage").asText())
                            .secondImage(commonItem.path("secondimage").isNull() ? null : commonItem.path("secondimage").asText())
                            .build();

                    festivalDTOList.add(festivalDTO);


                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            if(!festivalDTOList.isEmpty()){
                saveAll(festivalDTOList);
                log.info("축제 DB 동기화 완료 >>> {}", festivalDTOList.size());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
