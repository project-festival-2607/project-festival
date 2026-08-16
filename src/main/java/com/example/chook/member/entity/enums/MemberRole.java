package com.example.chook.member.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberRole {

    ADMIN("ROLE_ADMIN"),
    RECRUITER("ROLE_RECRUITER"),
    JOB_SEEKER("ROLE_JOB_SEEKER"),
    JOB_EQUIP("ROLE_JOB_EQUIP");

    private final String role;

}
