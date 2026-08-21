package com.example.chook.festival;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FestivalRepository extends JpaRepository<Festival, String>, FestivalCustomRepository {
  List<Festival> findByMember_Username(String username);

}
