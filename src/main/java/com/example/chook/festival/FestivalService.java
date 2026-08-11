package com.example.chook.festival;

import com.example.chook.chookMain.ChookMainDTO;
import com.example.chook.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public interface FestivalService {

    default FestivalDTO convertEntityToDTO(Festival festival){
        return FestivalDTO.builder()
                .contentId(festival.getContentId())
                .member(festival.getMember() != null ? festival.getMember().getId() : null)
                .title(festival.getTitle())
                .homepage(festival.getHomepage())
                .mapX(festival.getMapX())
                .mapY(festival.getMapY())
                .overview(festival.getOverview())
                .tel(festival.getTel())
                .telName(festival.getTelName())
                .ageLimit(festival.getAgeLimit())
                .eventPlace(festival.getEventPlace())
                .zipCode(festival.getZipCode())
                .address(festival.getAddress())
                .playTime(festival.getPlayTime())
                .program(festival.getProgram())
                .useTime(festival.getUseTime())
                .startDate(festival.getStartDate() != null ? festival.getStartDate().toString() : null)
                .endDate(festival.getEndDate() != null ? festival.getEndDate().toString() : null)
                .firstImage(festival.getFirstImage())
                .secondImage(festival.getSecondImage())
                .build();
    }

    default Festival convertDTOToEntity(FestivalDTO festivalDTO){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        LocalDate startDate = null;
        if(festivalDTO.getStartDate() != null && !festivalDTO.getStartDate().isBlank()){
            String start = festivalDTO.getStartDate().replace("-", "");
            startDate = LocalDate.parse(start, formatter);
        }

        LocalDate endDate = null;
        if(festivalDTO.getEndDate() != null && !festivalDTO.getEndDate().isBlank()){
            String end = festivalDTO.getEndDate().replace("-", "");
            endDate = LocalDate.parse(end, formatter);
        }

        Member member = null;
        if(festivalDTO.getMember() != null && festivalDTO.getMember() != 0L){
            member = Member.builder()
                    .id(festivalDTO.getMember())
                    .build();
        }


        return Festival.builder()
                .contentId(festivalDTO.getContentId())
                .member(member)
                .title(festivalDTO.getTitle())
                .homepage(festivalDTO.getHomepage())
                .mapX(festivalDTO.getMapX())
                .mapY(festivalDTO.getMapY())
                .overview(festivalDTO.getOverview())
                .tel(festivalDTO.getTel())
                .telName(festivalDTO.getTelName())
                .ageLimit(festivalDTO.getAgeLimit())
                .eventPlace(festivalDTO.getEventPlace())
                .zipCode(festivalDTO.getZipCode())
                .address(festivalDTO.getAddress())
                .playTime(festivalDTO.getPlayTime())
                .program(festivalDTO.getProgram())
                .useTime(festivalDTO.getUseTime())
                .startDate(startDate)
                .endDate(endDate)
                .firstImage(festivalDTO.getFirstImage())
                .secondImage(festivalDTO.getSecondImage())
                .build();
    }

    String save(FestivalDTO festivalDTO);

    void saveAll(List<FestivalDTO> festivalDTOList);

    Page<FestivalDTO> getList(int pageNo);

    FestivalDTO getDetail(String contentId);

    Page<FestivalDTO> getList(int pageNo, String type, String keyword, String month);

    void registerFes(FestivalDTO festivalDTO, MultipartFile file);

    void remove(String id);

    List<FestivalDTO> getAll();

    List<FestivalDTO> getByUsername(String username);

    List<ChookMainDTO> getMainList();
}
