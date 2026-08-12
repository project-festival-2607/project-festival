package com.example.chook.mypage;

import com.example.chook.member.entity.enums.MemberRole;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageDTO {

    // 아이디
    private String username;

    // 이름
    private String name;

    // 전화번호
    private String phone;

    // 포인트
    private Long point;

    // 계정유형
    private MemberRole role;

    // 비밀번호
    private String password;

    // 이메일
    private String email;
}
