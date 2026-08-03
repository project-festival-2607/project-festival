package com.example.chook.entity;

import com.example.chook.entity.enums.RecruitmentWageType;
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
  @Column(name = "recruit_id")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(
    name = "recruit_id",
    foreignKey = @ForeignKey(name = "fk_recruitment_individual_recruit_id")
  )
  private Recruitment recruit;

  @Enumerated(EnumType.STRING)
  @Column(name = "wage_type", nullable = false, length = 10)
  private RecruitmentWageType wageType;

  @Column(name = "wage_value")
  private int wageValue;

}
