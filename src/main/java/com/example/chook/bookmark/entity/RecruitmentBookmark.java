package com.example.chook.bookmark.entity;

import com.example.chook.member.entity.Member;
import com.example.chook.recruitment.entity.Recruitment;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
  name = "recruitment_bookmark",
  uniqueConstraints = {
    @UniqueConstraint(
      name = "uk_recruitment_bookmark_recruitment_member",
      columnNames = {"recruit_id", "member_id"}
    )
  }
)
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitmentBookmark {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "recruitment_bookmark_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "recruit_id",
    nullable = false,
    foreignKey = @ForeignKey(name = "fk_recruitment_bookmark_recruit_id")
  )
  @ToString.Exclude
  private Recruitment recruitment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "member_id",
    nullable = false,
    foreignKey = @ForeignKey(name = "fk_recruitment_bookmark_member_id")
  )
  @ToString.Exclude
  private Member member;

}
