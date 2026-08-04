package com.example.chook.support.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(nullable = false)
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
