package com.example.chook.payment.service;

import com.example.chook.member.entity.Member;
import com.example.chook.member.repository.MemberRepository;
import com.example.chook.payment.entity.PayClassify;
import com.example.chook.payment.repository.PayClassifyRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PayClassifyService {

    private final PayClassifyRepository payClassifyRepository;
    private final MemberRepository memberRepository;
    private final TransactionIdService transactionIdService;

    /**
     * 새로운 거래를 생성한다.
     *
     * 1. 회원 확인
     * 2. TransactionIdService를 통해 거래번호 발급
     * 3. PayClassify 생성
     * 4. DB 저장
     *
     * 예:
     *
     * memberId = 2
     * transactionType = "refund"
     *
     * -> transactionId = "user2-4"
     *
     * -> PayClassify 저장
     */
    @Transactional
    public PayClassify createTransaction(
            Long memberId,
            String transactionType
    ) {

        if (memberId == null) {
            throw new IllegalArgumentException(
                    "회원 ID가 없습니다."
            );
        }

        if (transactionType == null || transactionType.isBlank()) {
            throw new IllegalArgumentException(
                    "거래 종류가 없습니다."
            );
        }

        // 회원 조회
        Member member =
                memberRepository.findById(memberId)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "회원을 찾을 수 없습니다: "
                                                + memberId
                                )
                        );

        // 회원별 새로운 transactionId 발급
        String transactionId =
                transactionIdService.createTransactionId(memberId);

        // PayClassify 생성
        PayClassify payClassify =
                PayClassify.builder()
                        .member(member)
                        .transactionId(transactionId)
                        .transactionType(transactionType)
                        .build();

        // DB 저장
        return payClassifyRepository.save(payClassify);
    }
}