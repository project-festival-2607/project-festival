package com.example.chook.bookmark.controller;

import com.example.chook.bookmark.service.RecruitmentBookmarkService;
import com.example.chook.member.entity.Member;
import com.example.chook.member.entity.enums.MemberRole;
import com.example.chook.member.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookmark/recruitment")
@RequiredArgsConstructor
@Slf4j
public class RecruitmentBookmarkController {

  private final RecruitmentBookmarkService recruitmentBookmarkService;

  // 구직자만 찜하기 가능 (버튼도 구직자에게만 노출되지만, 여기서도 한번 더 확인)
  @PostMapping("/{id}")
  public ResponseEntity<Boolean> toggle(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
    if (!(user instanceof CustomUserDetails cud)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    Member member = cud.getMember();
    if (member.getRole() != MemberRole.JOB_SEEKER) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    boolean bookmarked = recruitmentBookmarkService.toggle(id, member.getId());
    log.info("recruitment bookmark toggled: recruitmentId={}, memberId={}, bookmarked={}", id, member.getId(), bookmarked);
    return ResponseEntity.ok(bookmarked);
  }

}