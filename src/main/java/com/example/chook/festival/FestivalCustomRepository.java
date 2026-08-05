package com.example.chook.festival;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FestivalCustomRepository {
    Page<Festival> searchFestival(String type, String keyword, Pageable pageable);
}
