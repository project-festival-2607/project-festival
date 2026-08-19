package com.example.chook.payment.service;
//거래마다 고유한 번호를 만들어줌.
import com.example.chook.member.entity.Member;
import com.example.chook.member.repository.MemberRepository;
import com.example.chook.payment.entity.MemberTransactionSequence;
import com.example.chook.payment.repository.MemberTransactionSequenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TransactionIdService {

    private final MemberTransactionSequenceRepository sequenceRepository;
    private final MemberRepository memberRepository;
    /**회원별 transactionId를 하나 발급한다.
     *
     * 예:
     * memberId = 1
     *
     * 첫 번째 호출 -> user1-1
     * 두 번째 호출 -> user1-2
     * 세 번째 호출 -> user1-3
     */
    @Transactional
    public String createTransactionId(Long memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException(
                    "회원 ID가 없습니다."
            );
        }


        // 해당 회원의 번호 관리 레코드 조회
        MemberTransactionSequence sequence = sequenceRepository
                        .findByMember_Id(memberId)  //회원의 마지막 거래번호가 몇 번이었는지 DB에서 찾음
                        .orElseGet(() -> {
                            // 회원 조회
                            Member member =
                                    memberRepository.findById(memberId)
                                            .orElseThrow(() ->
                                                    new NoSuchElementException(
                                                            "회원을 찾을 수 없습니다: "
                                                                    + memberId
                                                    )
                                            );

                            // 처음 거래하는 회원이면
                            // 번호 관리 레코드를 새로 생성
                            return MemberTransactionSequence.builder()
                                    .member(member)
                                    .lastTransactionId(0L)
                                    .build();
                        });


        // 다음 번호
        long nextNumber = sequence.getLastTransactionId() + 1;

        // 번호 업데이트
        sequence.setLastTransactionId(nextNumber);

        // DB 저장
        sequenceRepository.save(sequence);

        // transactionId 생성
        return "user" + memberId + "-" + nextNumber; //ex) user14-5  id가 14번인 유저의 5번째 행동.
    }
}