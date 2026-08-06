package com.example.chook.dev;

import com.example.chook.member.entity.BusinessRegistration;
import com.example.chook.member.entity.EmployerProfile;
import com.example.chook.member.entity.JobSeekerProfile;
import com.example.chook.member.entity.Member;
import com.example.chook.member.entity.enums.Gender;
import com.example.chook.member.entity.enums.MemberRole;
import com.example.chook.member.entity.enums.MemberStatus;
import com.example.chook.member.repository.BusinessRegistrationRepository;
import com.example.chook.member.repository.EmployerProfileRepository;
import com.example.chook.member.repository.JobSeekerProfileRepository;
import com.example.chook.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class DevMemberDataInitializer {

  private final MemberRepository memberRepository;
  private final JobSeekerProfileRepository jobSeekerProfileRepository;
  private final EmployerProfileRepository employerProfileRepository;
  private final BusinessRegistrationRepository businessRegistrationRepository;
  private final PasswordEncoder passwordEncoder;

  private final Random random = new Random();

  @Transactional
  public void generateSampleMembers(
    int recruiterCount,
    int jobSeekerCount,
    int jobEquipCount
  ) {

    if (memberRepository.count() == 0) {

      Set<Integer> generatedNumbers = new HashSet<>();

      for (int i = 0; i < recruiterCount + jobEquipCount; i++) {
        generatedNumbers.add(random.nextInt(100000));
      }

      Iterator<Integer> businessNumberGenerator = generatedNumbers.iterator();

      // RECRUITER
      for (int i = 1; i <= recruiterCount; i++) {
        Member member = addMember("r", "행사 구인자", MemberRole.RECRUITER, i);
        member.setPoint(50_000L);
        addEmployerProfile(member, i);
        addBusinessRegistration(member, businessNumberGenerator);

      }

      // JOB_SEEKER
      for (int i = 1; i <= jobSeekerCount; i++) {
        Member member = addMember("s", "일반 구직자", MemberRole.JOB_SEEKER, i);
        addIndividualProfile(member);
      }

      // JOB_EQUIP
      for (int i = 1; i <= jobEquipCount; i++) {
        Member member = addMember("e", "전문 구직자", MemberRole.JOB_EQUIP, i);
        addIndividualProfile(member);
        addBusinessRegistration(member, businessNumberGenerator);
      }

    }

    // username이 "admin"인 ADMIN 계정 추가
    // 이미 존재해도 삭제 후 다시 생성
    memberRepository.deleteByUsername("admin");
    memberRepository.flush();
    memberRepository.save(
      Member.builder()
        .username("admin")
        .passwordHash(passwordEncoder.encode("admin"))
        .name("관리자")
        .phone("01000000000")
        .phoneVerified(true)
        .email("admin@example.com")
        .role(MemberRole.ADMIN)
        .status(MemberStatus.ACTIVE)
        .point(1_000_000L)
        .build()
    );

  }

  private Member addMember(String usernamePrefix,
                           String namePrefix,
                           MemberRole role,
                           int index) {
    String username = String.format("%s%02d", usernamePrefix, index);

    return memberRepository.save(
      Member.builder()
        .username(username)
        .passwordHash(passwordEncoder.encode(username))
        .name(String.format("%s #%02d", namePrefix, index))
        .phone("01000000000")
        .phoneVerified(true)
        .email(String.format("%s@example.com", username))
        .role(role)
        .status(MemberStatus.ACTIVE)
        .build()
    );
  }

  private void addEmployerProfile(Member member, int index) {
    employerProfileRepository.save(
      EmployerProfile.builder()
        .member(member)
        .companyName(String.format("행사 주최 기업 #%02d", index))
        .ceoName(String.format("행사 대표자 #%02d", index))
        .streetAddress("-")
        .foundedAt(LocalDate.of(2000, 1, 1))
        .build()
    );
  }

  private void addIndividualProfile(Member member) {

    Gender[] genders = Gender.values();
    YearMonth yearMonth = YearMonth.of(
      random.nextInt(1950, Year.now().getValue() - 16 + 1),
      random.nextInt(1, 12 + 1)
    );

    jobSeekerProfileRepository.save(
      JobSeekerProfile.builder()
        .member(member)
        .gender(genders[random.nextInt(genders.length)])
        .birthDate(LocalDate.of(
          yearMonth.getYear(),
          yearMonth.getMonth(),
          random.nextInt(1, yearMonth.lengthOfMonth() + 1)
        ))
        .build()
    );

  }

  private void addBusinessRegistration(Member member, Iterator<Integer> iterator) {

    businessRegistrationRepository.save(
      BusinessRegistration.builder()
        .member(member)
        .businessNumber(String.format("TEST-%05d", iterator.next()))
        .verified(true)
        .verifiedAt(LocalDateTime.of(2026, 1, 1, 0, 0, 0))
        .build()
    );

  }
}
