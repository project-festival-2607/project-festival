package com.example.chook.entity;

import com.example.chook.entity.enums.Provider;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "social_logins",
        uniqueConstraints = {
                @UniqueConstraint(
                        // 제약조건 이름 설정
                        name = "uk_social_logins_provider_provider_id",
                        columnNames = {"provider", "provider_id"}
                )
        }
)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
      name = "member_id", 
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_social_logins_member_id")
    )
    @ToString.Exclude // 순환 참조 방지
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 10)
    private Provider provider;

    @Column(name = "provider_id", nullable = false, length = 255)
    private String providerId;

    @Column(name = "linked_at", nullable = false, updatable = false)
    private LocalDateTime linkedAt;
}
