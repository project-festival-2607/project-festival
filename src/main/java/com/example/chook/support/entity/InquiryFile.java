package com.example.chook.support.entity;

import com.example.chook.file.entity.UploadedFile;
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
@Table(name="inquiry_file")
public class InquiryFile {

    @Id
    @Column(name = "inquiry_file_pair_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
      name = "ino",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_inquiry_file_ino")
    )
    private Inquiry inquiry; //inquiry 테이블에서 ino => 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
      name = "file_uuid",
      nullable = false,
      columnDefinition = "BINARY(16)",
      foreignKey = @ForeignKey(name = "fk_inquiry_file_file_id")
    )
    private UploadedFile uploadedFile; //uploaded_file 테이블에서 uuid=> 외래키
}
