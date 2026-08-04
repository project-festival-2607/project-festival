package com.example.chook.member.entity.enums;

import lombok.Getter;

@Getter
public enum MemberRole {

    ADMIN("ROLE_ADMIN"),
    RECRUITER("ROLE_RECRUITER"),
    JOB_SEEKER("ROLE_JOB_SEEKER"),
    JOB_EQUIP("ROLE_JOB_EQUIP");

    private final String role;
    MemberRole(String role) {
        this.role = role;
    }
}
