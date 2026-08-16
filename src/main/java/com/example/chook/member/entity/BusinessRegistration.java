package com.example.chook.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "business_registrations")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessRegistration {

    @Id
    @Column(name = "member_id")
    private Long memberId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(
      name = "member_id",
      foreignKey = @ForeignKey(name = "fk_business_registrations_member_id")
    )
    @ToString.Exclude
    private Member member;

    @Column(name = "business_number", nullable = false, length = 10)
    private String businessNumber;

    @Column(name = "verified",  nullable = false)
    @Builder.Default
    private Boolean verified = false;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
