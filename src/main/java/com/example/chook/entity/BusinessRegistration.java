package com.example.chook.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "business_registrations")
@Getter
public class BusinessRegistration {

    @Id
    @Column(name = "member_id")
    private Long memberId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "business_number", nullable = false, length = 10)
    private String businessNumber;

    @Column(name = "verified",  nullable = false)
    private Boolean verified = false;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
