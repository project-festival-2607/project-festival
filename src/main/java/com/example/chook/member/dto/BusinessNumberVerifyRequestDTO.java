package com.example.chook.member.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessNumberVerifyRequestDTO {

    private String businessNumber;

}
