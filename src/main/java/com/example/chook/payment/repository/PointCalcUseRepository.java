package com.example.chook.payment.repository;

import com.example.chook.payment.entity.PointCalcUse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointCalcUseRepository extends JpaRepository<PointCalcUse, Integer> {

    // 해당 회원의 사용 가능한 포인트를
    // 오래된 결제부터 조회
    List<PointCalcUse> findByMember_IdAndLeftPointGreaterThanOrderByCreatedAtAsc(
            Long memberId,
            Integer leftPoint
    );
}