package com.example.chook.entity;

import com.example.chook.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "job_seeker_profiles")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSeekerProfile {

    @Id
    @Column(name = "member_id")
    private Long memberId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(
      name = "member_id",
      foreignKey = @ForeignKey(name = "fk_job_seeker_profiles_member_id")
    )
    @ToString.Exclude
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "street_address", length = 255)
    private String streetAddress;

    @Column(name = "detail_address", length = 255)
    private String detailAddress;

}
