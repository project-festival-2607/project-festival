package com.example.chook.member.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    private String username;
    private String password;

}
