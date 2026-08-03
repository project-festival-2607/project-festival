package com.example.chook.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "employer_profiles")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployerProfile {

    @Id
    @Column(name = "member_id")
    private Long memberId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(
      name = "member_id",
      foreignKey = @ForeignKey(name = "fk_employer_profiles_member_id")
    )
    @ToString.Exclude
    private Member member;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "ceo_name", nullable = false, length = 50)
    private String ceoName;

    @Column(name = "street_address", nullable = false, length = 255)
    private String streetAddress;

    @Column(name = "detail_address", length = 255)
    private String detailAddress;

    @Column(name = "founded_at", nullable = false)
    private LocalDate foundedAt;
}
