package com.example.chook.member.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployerProfileUpdateRequestDTO {

    private String email;
    private String companyName;
    private String ceoName;
    private String streetAddress;
    private String detailAddress;

}
