package com.example.chook.entity;

import com.example.chook.entity.enums.MemberRole;
import com.example.chook.entity.enums.MemberStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "members",
        uniqueConstraints = {
                @UniqueConstraint(
                        // 제약조건 이름 설정
                        name = "uk_members_username",
                        columnNames = {"username"}
                )
        }
)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "phone_verified", nullable = false)
    private boolean phoneVerified = false;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MemberStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "point", nullable = false)
    // 오버플로우 리스크 방지를 위해 Long 사용
    // 결제 시스템의 금액 컬럼들은 보통 Long이나 BigDecimal 사용
    private Long point = 0L;

    @OneToMany(mappedBy = "member")
    // 소셜 연동 리스트 컬렉션 초기화
    private List<SocialLogin> socialLogins =  new ArrayList<>();
}
