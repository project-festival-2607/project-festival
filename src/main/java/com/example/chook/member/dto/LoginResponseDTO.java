package com.example.chook.member.dto;

import com.example.chook.member.entity.enums.MemberRole;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    private Long id;
    private String username;
    private String name;
    private MemberRole role;

}
