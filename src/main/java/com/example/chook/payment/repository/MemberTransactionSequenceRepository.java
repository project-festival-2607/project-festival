package com.example.chook.payment.repository;

import com.example.chook.payment.entity.MemberTransactionSequence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberTransactionSequenceRepository
        extends JpaRepository<MemberTransactionSequence, Long> {

    Optional<MemberTransactionSequence> findByMember_Id(Long memberId);
}