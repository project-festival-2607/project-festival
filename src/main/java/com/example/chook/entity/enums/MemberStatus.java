package com.example.chook.entity.enums;

import lombok.Getter;

@Getter
public enum MemberStatus {

    ACTIVE("STATUS_ACTIVE"),
    DORMANT("STATUS_DORMANT"),
    SUSPENDED("STATUS_SUSPENDED");

    private final String status;
    MemberStatus(String status) {
        this.status = status;
    }
}
