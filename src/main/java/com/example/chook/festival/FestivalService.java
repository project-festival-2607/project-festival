package com.example.chook.festival;

import com.example.chook.entity.Festival;

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
                .startDate(festival.getStartDate())
                .endDate(festival.getEndDate())
                .firstImage(festival.getFirstImage())
                .secondImage(festival.getSecondImage())
                .build();
    }

    default Festival convertDTOToEntity(FestivalDTO festivalDTO){
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
                .startDate(festivalDTO.getStartDate())
                .endDate(festivalDTO.getEndDate())
                .firstImage(festivalDTO.getFirstImage())
                .secondImage(festivalDTO.getSecondImage())
                .build();
    }

    String save(FestivalDTO festivalDTO);

    void saveAll(List<FestivalDTO> festivalDTOList);
}
