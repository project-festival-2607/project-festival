package com.example.chook.member.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberRole {

    ADMIN("ROLE_ADMIN", "관리자"),
    RECRUITER("ROLE_RECRUITER", "행사 구인자"),
    JOB_SEEKER("ROLE_JOB_SEEKER", "일반 구직자"),
    JOB_EQUIP("ROLE_JOB_EQUIP", "전문 구직자");

    private final String role;
    private final String label;

}
