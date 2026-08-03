package com.example.chook.festival;

import com.example.chook.entity.Festival;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public interface FestivalService {

    default FestivalDTO convertEntityToDTO(Festival festival){
        return FestivalDTO.builder()
                .contentId(festival.getContentId())
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
                .playTime(festival.getPlayTime())
                .program(festival.getProgram())
                .useTime(festival.getUseTime())
                .startDate(festival.getStartDate().toString())
                .endDate(festival.getEndDate().toString())
                .firstImage(festival.getFirstImage())
                .secondImage(festival.getSecondImage())
                .build();
    }

    default Festival convertDTOToEntity(FestivalDTO festivalDTO){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        return Festival.builder()
                .contentId(festivalDTO.getContentId())
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
                .playTime(festivalDTO.getPlayTime())
                .program(festivalDTO.getProgram())
                .useTime(festivalDTO.getUseTime())
                .startDate(LocalDate.parse(festivalDTO.getStartDate(), formatter))
                .endDate(LocalDate.parse(festivalDTO.getEndDate(), formatter))
                .firstImage(festivalDTO.getFirstImage())
                .secondImage(festivalDTO.getSecondImage())
                .build();
    }

    String save(FestivalDTO festivalDTO);

    void saveAll(List<FestivalDTO> festivalDTOList);

    Page<FestivalDTO> getList(int pageNo);
}
