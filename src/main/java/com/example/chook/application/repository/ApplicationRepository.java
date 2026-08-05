package com.example.chook.application.repository;

import com.example.chook.application.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // 내가 지원한 목록 조회
    List<Application> findByMemberId(Long memberId);
}
