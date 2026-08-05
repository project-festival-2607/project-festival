package com.example.chook.support.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="inquiry")
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ino;

    // ponytail: 로그인 미구현 - 회원 연결 전까지 null 허용, 로그인 붙으면 nullable = false로 되돌리기
    @Column
    private Long id; //user 테이블에서 id => 외래키

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(length = 2000)
    private String comment;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name="edited_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "commment_created_at")
    private LocalDateTime commentTime;
}
