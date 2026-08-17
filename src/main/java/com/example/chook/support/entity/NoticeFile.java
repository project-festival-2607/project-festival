package com.example.chook.support.entity;


import com.example.chook.file.entity.UploadedFile;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="admin_board_file")
public class NoticeFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="admin_board_file_pair_id")
    private Long id; //식별 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
      name = "bno",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_admin_board_file_bno")
    )
    private Notice notice; //admin_board 테이블에서 bno 외래키

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
      name = "file_uuid",
      nullable = false,
      unique = true,
      columnDefinition = "BINARY(16)",
      foreignKey = @ForeignKey(name = "fk_admin_board_file_file_id")
    )
    private UploadedFile uploadedFile; // uploaded_file 테이블에서 file_id => 외래키, 파일 하나당 게시글 한 곳에만 연결
}
