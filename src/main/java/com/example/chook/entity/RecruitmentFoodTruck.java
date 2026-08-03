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
@Table(name = "recruitment_food_truck")
public class RecruitmentFoodTruck {

  @Id
  @Column(name = "recruit_id")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(
    name = "recruit_id",
    foreignKey = @ForeignKey(name = "fk_recruitment_food_truck_recruit_id")
  )
  private Recruitment recruit;

  @Column(nullable = false)
  private boolean prepaid;

  @Column(name = "booth_fee_required", nullable = false)
  private boolean boothFeeRequired;

  @Column(name = "electricity_provided", nullable = false)
  private boolean electricityProvided;

}
