package com.example.chook.bookmark.entity;

import com.example.chook.festival.Festival;
import com.example.chook.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
  name = "festival_bookmark",
  uniqueConstraints = {
    @UniqueConstraint(
      name = "uk_festival_bookmark_festival_member",
      columnNames = {"festival_content_id", "member_id"}
    )
  }
)
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FestivalBookmark {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "festival_bookmark_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "festival_content_id",
    nullable = false,
    foreignKey = @ForeignKey(name = "fk_festival_bookmark_festival_content_id")
  )
  @ToString.Exclude
  private Festival festival;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "member_id",
    nullable = false,
    foreignKey = @ForeignKey(name = "fk_festival_bookmark_member_id")
  )
  @ToString.Exclude
  private Member member;

}
