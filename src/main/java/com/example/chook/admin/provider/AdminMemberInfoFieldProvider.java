package com.example.chook.admin.provider;

import com.example.chook.admin.record.ModalInfoField;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminMemberInfoFieldProvider {

  public List<ModalInfoField> suspendMember() {
    return List.of(
      new ModalInfoField("아이디", "username"),
      new ModalInfoField("이름", "name"),
      new ModalInfoField("이메일", "email"),
      new ModalInfoField("휴대전화번호", "phone"),
      new ModalInfoField("최근접속일시", "lastLoginAt")
    );
  }

  public List<ModalInfoField> unsuspendMember() {
    return List.of(
      new ModalInfoField("아이디", "username"),
      new ModalInfoField("이름", "name"),
      new ModalInfoField("정지일시", "suspendedAt"),
      new ModalInfoField("정지사유", "suspendedReason")
    );
  }

  public List<ModalInfoField> removePhoneVerificationMember() {
    return List.of(
      new ModalInfoField("아이디", "username"),
      new ModalInfoField("이름", "name"),
      new ModalInfoField("휴대전화번호", "phone")
    );
  }

  public List<ModalInfoField> addPhoneWithVerificationMember() {
    return List.of(
      new ModalInfoField("아이디", "username"),
      new ModalInfoField("이름", "name"),
      new ModalInfoField("기존 휴대전화번호", "phone")
    );
  }

  public List<ModalInfoField> removeBusinessRegistrationMember() {
    return List.of(
      new ModalInfoField("아이디", "username"),
      new ModalInfoField("이름", "name"),
      new ModalInfoField("사업자등록번호", "businessNumber"),
      new ModalInfoField("인증 일시", "businessNumberVerifiedAt")
    );
  }

  public List<ModalInfoField> removeBusinessRegistrationWithCompanyInfoMember() {
    return List.of(
      new ModalInfoField("아이디", "username"),
      new ModalInfoField("이름", "name"),
      new ModalInfoField("상호명", "companyName"),
      new ModalInfoField("대표자명", "ceoName"),
      new ModalInfoField("사업자등록번호", "businessNumber"),
      new ModalInfoField("인증 일시", "businessNumberVerifiedAt")
    );
  }

  public List<ModalInfoField> addBusinessRegistrationMember() {
    return List.of(
      new ModalInfoField("아이디", "username"),
      new ModalInfoField("이름", "name")
    );
  }

  public List<ModalInfoField> addBusinessRegistrationWithCompanyInfoMember() {
    return List.of(
      new ModalInfoField("아이디", "username"),
      new ModalInfoField("이름", "name"),
      new ModalInfoField("상호명", "companyName"),
      new ModalInfoField("대표자명", "ceoName")
    );
  }

  public List<ModalInfoField> assignFestivalManagerFestival() {
    return List.of(
      new ModalInfoField("고유 아이디", "contentId"),
      new ModalInfoField("행사 제목", "title"),
      new ModalInfoField("행사 상세주소", "address"),
      new ModalInfoField("행사 장소", "eventPlace"),
      new ModalInfoField("행사 시작일", "startDate"),
      new ModalInfoField("행사 종료일", "endDate")
    );
  }

  public List<ModalInfoField> assignFestivalManagerMember() {
    return List.of(
      new ModalInfoField("아이디", "username"),
      new ModalInfoField("이름", "name"),
      new ModalInfoField("상호명", "companyName"),
      new ModalInfoField("대표자명", "ceoName"),
      new ModalInfoField("사업자등록번호", "businessNumber"),
      new ModalInfoField("인증 일시", "businessNumberVerifiedAt")
    );
  }
}
