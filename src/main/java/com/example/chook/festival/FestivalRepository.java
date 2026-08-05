package com.example.chook.festival;

import org.springframework.data.jpa.repository.JpaRepository;


public interface FestivalRepository extends JpaRepository<Festival, String>, FestivalCustomRepository {
}
