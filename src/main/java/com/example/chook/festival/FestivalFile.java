package com.example.chook.festival;

import com.example.chook.file.entity.UploadedFile;
import com.example.chook.recruitment.entity.Recruitment;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "festival_file")
public class FestivalFile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "festival_file_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "content_id",
    nullable = false,
    foreignKey = @ForeignKey(name = "fk_festival_file_festival_content_id")
  )
  @ToString.Exclude
  private Festival festival;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(
    name = "file_uuid",
    nullable = false,
    unique = true,
    foreignKey = @ForeignKey(name = "fk_festival_file_file_uuid")
  )
  @ToString.Exclude
  private UploadedFile uploadedFile;
}
