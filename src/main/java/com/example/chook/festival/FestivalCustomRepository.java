package com.example.chook.festival;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface FestivalCustomRepository {
    Page<Festival> searchFestival(String type, String keyword, String month, Pageable pageable);
    List<Festival> findActiveFestivalsTop9(LocalDate today);
}
