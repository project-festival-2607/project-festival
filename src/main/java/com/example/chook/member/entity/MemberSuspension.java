package com.example.chook.member.entity;

import com.example.chook.common.entity.TimeBase;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member_suspension")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSuspension extends TimeBase {

  @Id
  @Column(name = "member_id")
  private Long memberId;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(
    name = "member_id",
    foreignKey = @ForeignKey(name = "fk_suspended_reason_member_id")
  )
  @ToString.Exclude
  private Member member;

  @Column(length = 255)
  private String reason;

}
