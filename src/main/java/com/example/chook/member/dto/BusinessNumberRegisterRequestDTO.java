package com.example.chook.member.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessNumberRegisterRequestDTO {

    private String businessNumber;
    private String verificationToken;

}
