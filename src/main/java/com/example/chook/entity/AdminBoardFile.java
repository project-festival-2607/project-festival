package com.example.chook.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="admin_board_file")
public class AdminBoardFile {
    @Id
    @Column(name="admin_board_file_pair_id")
    private Long id; //식별 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
      name = "bno",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_admin_board_file_bno")
    )
    private AdminBoard adminBoard; //admin_board 테이블에서 bno 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
      name = "file_uuid",
      nullable = false,
      columnDefinition = "BINARY(16)",
      foreignKey = @ForeignKey(name = "fk_admin_board_file_file_id")
    )
    private UploadedFile uploadedFile; // uploaded_file 테이블에서 file_id => 외래키
}
