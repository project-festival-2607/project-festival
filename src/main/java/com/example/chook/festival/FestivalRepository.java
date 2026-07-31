package com.example.chook.festival;

import com.example.chook.entity.Festival;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FestivalRepository extends JpaRepository<Festival, String> {
}
