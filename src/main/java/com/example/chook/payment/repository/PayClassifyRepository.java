package com.example.chook.payment.repository;

import com.example.chook.payment.entity.PayClassify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayClassifyRepository
        extends JpaRepository<PayClassify, Long> {

}