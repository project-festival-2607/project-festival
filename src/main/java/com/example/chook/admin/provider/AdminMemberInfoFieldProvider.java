package com.example.chook.admin.provider;

import com.example.chook.admin.record.MemberInfoField;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminMemberInfoFieldProvider {

  public List<MemberInfoField> suspend() {
    return List.of(
      new MemberInfoField("아이디", "username"),
      new MemberInfoField("이름", "name"),
      new MemberInfoField("이메일", "email"),
      new MemberInfoField("휴대전화번호", "phone"),
      new MemberInfoField("최근접속일시", "lastLoginAt")
    );
  }

  public List<MemberInfoField> unsuspend() {
    return List.of(
      new MemberInfoField("아이디", "username"),
      new MemberInfoField("이름", "name"),
      new MemberInfoField("정지일시", "suspendedAt"),
      new MemberInfoField("정지사유", "suspendedReason")
    );
  }

  public List<MemberInfoField> removePhoneVerification() {
    return List.of(
      new MemberInfoField("아이디", "username"),
      new MemberInfoField("이름", "name"),
      new MemberInfoField("휴대전화번호", "phone")
    );
  }

  public List<MemberInfoField> addPhoneWithVerification() {
    return List.of(
      new MemberInfoField("아이디", "username"),
      new MemberInfoField("이름", "name"),
      new MemberInfoField("기존 휴대전화번호", "phone")
    );
  }

  public List<MemberInfoField> removeBusinessRegistration() {
    return List.of(
      new MemberInfoField("아이디", "username"),
      new MemberInfoField("이름", "name"),
      new MemberInfoField("사업자등록번호", "businessNumber"),
      new MemberInfoField("인증 일시", "businessNumberVerifiedAt")
    );
  }

  public List<MemberInfoField> removeBusinessRegistrationWithCompanyInfo() {
    return List.of(
      new MemberInfoField("아이디", "username"),
      new MemberInfoField("이름", "name"),
      new MemberInfoField("상호명", "companyName"),
      new MemberInfoField("대표자명", "ceoName"),
      new MemberInfoField("사업자등록번호", "businessNumber"),
      new MemberInfoField("인증 일시", "businessNumberVerifiedAt")
    );
  }

  public List<MemberInfoField> addBusinessRegistration() {
    return List.of(
      new MemberInfoField("아이디", "username"),
      new MemberInfoField("이름", "name")
    );
  }

  public List<MemberInfoField> addBusinessRegistrationWithCompanyInfo() {
    return List.of(
      new MemberInfoField("아이디", "username"),
      new MemberInfoField("이름", "name"),
      new MemberInfoField("상호명", "companyName"),
      new MemberInfoField("대표자명", "ceoName")
    );
  }
}
