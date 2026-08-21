package com.example.chook.member.service;

import com.example.chook.member.BusinessNumberTokenProvider;
import com.example.chook.member.dto.BusinessNumberVerifyRequestDTO;
import com.example.chook.member.dto.BusinessNumberVerifyResponseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class BusinessNumberVerifyServiceImpl implements BusinessNumberVerifyService {

    // 국세청 사업자등록 상태조회 API에서 "계속사업자" 의미
    private static final String ACTIVE_STATUS_CODE = "01"; // 계속사업자

    // API 요청 주소
    // {key} -> application.properties에 설정한 api 키
    private static final String STATUS_URI = "https://api.odcloud.kr/api/nts-businessman/v1/status?serviceKey={key}";

    // 사업자 등록번호 인증 토큰 발급 객체
    private final BusinessNumberTokenProvider tokenProvider;

    // api키
    private final String apiKey;

    // 외부 HTTPS API를 호출하는 RestClient 객체 생성
    private final RestClient restClient = RestClient.create();

    public BusinessNumberVerifyServiceImpl(
            BusinessNumberTokenProvider tokenProvider,
            @Value("${business-number.api-key}") String apiKey
    ) {
        this.tokenProvider = tokenProvider;
        this.apiKey = apiKey;
    }

    @Override
    public BusinessNumberVerifyResponseDTO verify(BusinessNumberVerifyRequestDTO requestDTO) {

        // DTO에서 사용자가 입력한 사업자등록번호 가져오기
        String businessNumber = requestDTO.getBusinessNumber();

        // 국세청 API 응답을 저장할 변수
        NtsStatusResponse response;
        try {
            // POST 요청
            response = restClient.post()
                    // {key}에 apiKey 저장
                    .uri(STATUS_URI, apiKey)
                    // 요청 본문 데이터 형식을 JSON으로 지정
                    .contentType(MediaType.APPLICATION_JSON)
                    // API가 요구하는 JSON 형태로 사업자 번호를 전달
                    // {"b_no": ["0123456789"]}
                    .body(Map.of("b_no", List.of(businessNumber)))
                    // API 요청 전송
                    .retrieve()
                    // retrieve()가 받은 JSON 응답을 NtsStatusResponse 객체로 변환
                    .body(NtsStatusResponse.class);
        } catch (Exception e) {

            // 오류로 요청이 실패한 경우 서버 로그에 예외 내용 기록
            log.error("사업자등록상태조회 API 호출 실패", e);

            // 사용자에게는 일반적인 안내 메시지 반환
            return BusinessNumberVerifyResponseDTO
                    .builder()
                    .verified(false)
                    .message("인증 서버 호출에 실패했습니다. 잠시 후 다시 시도해주세요.")
                    .build();
        }

        // API 응답 없음 or data 없음 or data null의 경우
        // 조회 결과가 없는 것으로 처리
        if (response == null || response.data() == null || response.data().isEmpty()) {
            return BusinessNumberVerifyResponseDTO
                    .builder()
                    .verified(false)
                    .message("등록되지 않은 사업자번호입니다.")
                    .build();
        }

        // API 응답의 data 배열에서 사업자 정보 가져오기
        NtsStatusData data = response.data().get(0);

        // 사업자 상태 코드가 "01"이 아니라면(ACTIVE_STATUS_CODE와 다름)
        // 휴업 또는 폐업이므로 인증 실패 처리
        if (!ACTIVE_STATUS_CODE.equals(data.statusCode())) {
            return BusinessNumberVerifyResponseDTO
                    .builder()
                    .verified(false)
                    .message("휴업 또는 폐업 상태의 사업자입니다.")
                    .build();
        }

        // 계속사업자로 확인되었으므로 토큰 발급
        String token = tokenProvider.sign(businessNumber);

        // 인증 성공 결과를 DTO로 만들어 반환
        return BusinessNumberVerifyResponseDTO
                .builder()
                .verified(true)
                .verificationToken(token)
                .build();
    }

    // API 응답 전체를 담는 내부 record
    private record NtsStatusResponse(
            @JsonProperty("status_code") String resultCode,
            @JsonProperty("match_cnt") int matchCount,
            List<NtsStatusData> data
    ) {}

    // data 배열에 들어있는
    // 사업자 하나의 상태 정보를 담는 내부 record
    private record NtsStatusData(
            @JsonProperty("b_no") String businessNumber,
            @JsonProperty("b_stt") String status,
            @JsonProperty("b_stt_cd") String statusCode
    ) {}

}