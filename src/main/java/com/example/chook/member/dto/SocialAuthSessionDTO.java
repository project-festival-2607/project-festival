package com.example.chook.member.dto;

import com.example.chook.member.entity.enums.Provider;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialAuthSessionDTO {

    private Provider provider;
    private String providerId;
    private String email;
    private String name;

}
