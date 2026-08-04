package com.example.chook.member.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessNumberVerifyResponseDTO {

    private boolean verified;
    private String verificationToken;
    private String message;

}
