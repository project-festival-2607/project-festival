package com.example.chook.mypage.service;

import com.example.chook.member.entity.Member;
import com.example.chook.member.entity.enums.MemberRole;
import com.example.chook.member.repository.BusinessRegistrationRepository;
import com.example.chook.member.repository.EmployerProfileRepository;
import com.example.chook.member.repository.JobSeekerProfileRepository;
import com.example.chook.member.repository.MemberRepository;
import com.example.chook.mypage.dto.MyPageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class MyPageServiceImpl implements MyPageService {

    private final MemberRepository memberRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final BusinessRegistrationRepository businessRegistrationRepository;

    @Override
    public MyPageDTO getMyPage(String username) {

        Member member = memberRepository
                .findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow();

        MyPageDTO myPageDTO = memberEntityToDTO(member);

        if (member.getRole() == MemberRole.RECRUITER) {
            employerProfileRepository.findById(member.getId()).ifPresent(profile -> {
                myPageDTO.setCompanyName(profile.getCompanyName());
                myPageDTO.setCeoName(profile.getCeoName());
                myPageDTO.setStreetAddress(profile.getStreetAddress());
                myPageDTO.setDetailAddress(profile.getDetailAddress());
                myPageDTO.setFoundedAt(profile.getFoundedAt());
            });
            businessRegistrationRepository.findById(member.getId())
                    .ifPresent(reg -> myPageDTO.setBusinessNumber(reg.getBusinessNumber()));
        } else {
            jobSeekerProfileRepository.findById(member.getId()).ifPresent(profile -> {
                myPageDTO.setGender(profile.getGender());
                myPageDTO.setBirthDate(profile.getBirthDate());
                myPageDTO.setStreetAddress(profile.getStreetAddress());
                myPageDTO.setDetailAddress(profile.getDetailAddress());
            });
            if (member.getRole() == MemberRole.JOB_EQUIP) {
                businessRegistrationRepository.findById(member.getId())
                        .ifPresent(reg -> myPageDTO.setBusinessNumber(reg.getBusinessNumber()));
            }
        }

        return myPageDTO;
    }
}