package com.example.chook.member.exception;

public class MemberDormantException extends RuntimeException {

    public MemberDormantException() {
        super("휴면 계정입니다. 본인인증이 필요합니다.");
    }

}
