package com.example.chook.payment.repository;

// role 컬럼(enum 매핑 문제)을 건드리지 않기 위한 최소 정보 projection
public interface EligibleMemberView {
    Long getId();
    String getUsername();
    String getName();
}