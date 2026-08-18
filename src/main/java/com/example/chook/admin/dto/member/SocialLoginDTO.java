package com.example.chook.admin.dto.member;

import com.example.chook.member.entity.enums.Provider;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocialLoginDTO {

  Provider provider;
  LocalDateTime linkedAt;

}
