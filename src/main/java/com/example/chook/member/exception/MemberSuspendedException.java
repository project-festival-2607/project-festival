package com.example.chook.member.exception;

public class MemberSuspendedException extends RuntimeException {

    public MemberSuspendedException() {
        super("정지된 계정입니다.");
    }
}
