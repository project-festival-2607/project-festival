package com.example.chook.member.dto;

import com.example.chook.member.entity.enums.Provider;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindIdResultDTO {
    private boolean verified;      // 공개(reveal) 시에만 의미 있음
    private Long memberId;
    private boolean social;
    private String maskedUsername; // 검색 시엔 마스킹, 공개 시엔 전체 아이디
    private Provider provider;     // 소셜 회원이 공개됐을 때만 값 있음
}