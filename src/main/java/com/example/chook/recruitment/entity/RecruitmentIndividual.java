package com.example.chook.recruitment.entity;

import com.example.chook.recruitment.entity.enums.RecruitmentWageType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "recruitment_individual")
public class RecruitmentIndividual {

  @Id
  @Column(name = "recruitment_id")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(
    name = "recruitment_id",
    foreignKey = @ForeignKey(name = "fk_recruitment_individual_recruitment_id")
  )
  @ToString.Exclude
  private Recruitment recruitment;

  @Enumerated(EnumType.STRING)
  @Column(name = "wage_type", nullable = false, length = 10)
  private RecruitmentWageType wageType;

  @Column(name = "wage_value", nullable = false)
  private int wageValue;

}
