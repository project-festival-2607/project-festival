package com.example.chook.member.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MemberStatus {

    ACTIVE("활동", "member-status-active"),
    DORMANT("휴면", "member-status-dormant"),
    SUSPENDED("정지", "member-status-suspended");

    private final String label;
    private final String badgeClass;
}
