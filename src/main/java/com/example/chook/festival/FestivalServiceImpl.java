package com.example.chook.festival;

import com.example.chook.chookMain.ChookMainDTO;
import com.example.chook.file.entity.UploadedFile;
import com.example.chook.file.service.FileService;
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
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
//public class FestivalServiceImpl implements FestivalService, ApplicationRunner
public class FestivalServiceImpl implements FestivalService, ApplicationRunner {

    private final FestivalRepository festivalRepository;
    private final FileService fileService;
    private final FestivalFileRepository festivalFileRepository;

    private static final String RELATIVE_PATH = "festival";

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

    @Override
    public Page<FestivalDTO> getList(int pageNo, String type, String keyword, String month) {
        Pageable pageable = PageRequest.of(pageNo - 1, 18, Sort.by("startDate").ascending());
        Page<Festival> pageList = festivalRepository.searchFestival(type, keyword, month, pageable);
        return pageList.map(this::convertEntityToDTO);
    }

    @Transactional
    @Override
    public void registerFes(FestivalDTO festivalDTO, MultipartFile file) {

        // 행사 등록
        if(festivalDTO.getContentId() == null || festivalDTO.getContentId().isBlank()){
            festivalDTO.setContentId(UUID.randomUUID().toString());
        }
        Festival festival = convertDTOToEntity(festivalDTO);
        Festival savedFestival = festivalRepository.save(festival);

        // 이미지 연결
        if (file != null && !file.isEmpty()) {
            UploadedFile uploadedFile = fileService.upload(file, RELATIVE_PATH);
            festivalFileRepository.save(
              FestivalFile.builder()
                .festival(savedFestival)
                .uploadedFile(uploadedFile)
                .build()
            );
            String url = String.format("/festival/image/%s", uploadedFile.getUuid());
            savedFestival.setFirstImage(url);

        }
    }

    @Override
    public void remove(String id) {
        Festival festival = festivalRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 축제가 존재하지 않습니다."));

        festivalRepository.delete(festival);
    }

//    DB에서 API 요청 후 백그라운드에서 동기화

    @Override
    public void run(@NonNull ApplicationArguments args) throws Exception{
//        if (festivalRepository.count() > 0) {
//            log.info("DB 데이터 동기 완료");
//            return;
//            // 나중에 새로 갱신될 때를 대비하여 ID로 비교하는 로직으로 바꿀 것! ===> 지금은 TEST
//        }

        try {
            // API 요청한도를 방지하기 위한 개선 ID비교 로직 추가
            List<Festival> festivalDBList = festivalRepository.findAll();
            Set<String> festivalDBIds = festivalDBList.stream().map(Festival::getContentId).collect(Collectors.toSet());

            String url = "https://apis.data.go.kr/B551011/KorService2/searchFestival2?numOfRows=110&MobileOS=WEB&MobileApp=CHUCK&_type=json&arrange=R&eventStartDate=20260101&serviceKey=" + apiKey;

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

                if(contentId != null && festivalDBIds.contains(contentId)) {
                    log.info("이미 등록된 축제 데이터를 건너뛰고 있습니다... {}", contentId);
                    continue;
                }

                log.info("신규 축제 데이터 수집 시작...");

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
                            .address(commonItem.path("addr1").isNull() ? null : commonItem.path("addr1").asText())
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
                    log.error("축제 데이터 수집 오류 : {}", contentId, e);
                }
            }

            if(!festivalDTOList.isEmpty()){
                saveAll(festivalDTOList);
                log.info("축제 DB 동기화 완료 >>> {}건", festivalDTOList.size());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // map Zone
    @Override
    public List<FestivalDTO> getAll() {
        return festivalRepository.findAll()
                .stream()
                .map(this::convertEntityToDTO)
                .toList();
    }


    @Override
    public List<FestivalDTO> getByUsername(String username) {
        return festivalRepository.findByMember_Username(username)
                .stream()
                .map(this::convertEntityToDTO)
                .toList();
    }

    @Override
    public List<ChookMainDTO> getMainList() {

        LocalDate today = LocalDate.now();

        return festivalRepository.findActiveFestivalsTop9(today)
                .stream()
                .map((fes) -> new ChookMainDTO(
                        fes.getContentId(),
                        fes.getTitle(),
                        String.valueOf(fes.getStartDate()),
                        String.valueOf(fes.getEndDate()),
                        fes.getFirstImage(),
                        fes.getProgram()
                ))
                .collect(Collectors.toList());
    }

}
