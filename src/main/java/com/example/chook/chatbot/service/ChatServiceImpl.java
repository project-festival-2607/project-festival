package com.example.chook.chatbot.service;

import com.example.chook.chatbot.QuestionClassifier;
import com.example.chook.chatbot.QuestionType;
import com.example.chook.chatbot.dto.ChatRequestDTO;
import com.example.chook.chatbot.dto.ChatResponseDTO;
import com.example.chook.chatbot.dto.MessageDTO;
import com.example.chook.chatbot.entity.ChatHistory;
import com.example.chook.festival.FestivalDTO;
import com.example.chook.festival.FestivalService;
import com.example.chook.recruitment.entity.Recruitment;
import com.example.chook.recruitment.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final WebClient webClient;
    private final ChatHistoryService chatHistoryService;
    private final FestivalService festivalService;
    private final QuestionClassifier questionClassifier;
    private final RecruitmentRepository recruitmentRepository;

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.model}")
    private String model;

    @Override
    public ChatResponseDTO ask(ChatRequestDTO chatRequestDTO, Long memberId, String sessionId) {

        // 사용자가 보낸 질문 가져오기
        String question = chatRequestDTO.getMessages()
                        .get(chatRequestDTO.getMessages().size() - 1)
                        .getContent();
        log.info("사용자 질문 = {}", question);

        // 이전 대화 조회
        List<ChatHistory> historyList = chatHistoryService.getLastChat(sessionId);

        String festivalTitle = null;
        if (historyList != null && !historyList.isEmpty()) {
            for (ChatHistory history : historyList) {

                log.info("이전 질문 = {}", history.getQuestion());
                log.info("이전 답변 = {}", history.getAnswer());

                festivalTitle = extractFestivalTitle(history.getQuestion());

                if (festivalTitle != null) {
                    break;
                }
            }
            log.info("이전 대화에서 찾은 축제명 = {}", festivalTitle);
        }

        // 질문 분류
        QuestionType questionType = questionClassifier.classify(question);

        log.info("질문 분류 결과 = {}", questionType);

        // 이전 축제명을 이용한 후속 알바 질문 처리
        if (questionType == QuestionType.JOB && festivalTitle != null) {
            if (question.contains("여기")
                    || question.contains("거기")
                    || question.contains("그 축제")
                    || question.contains("시급")
                    || question.contains("일급")
                    || question.contains("급여")) {
                question = festivalTitle + " " + question.replace("여기", "");
            }
        }
        log.info("최종 검색 질문 = {}", question);

        // Groq에게 전달할 기본 prompt

        String prompt = question;
        String searchQuestion = question;

        // 질문 종류별 처리
        switch (questionType) {

            // FESTIVAL
            case FESTIVAL:
                // DB에서 전체 축제 조회
                List<FestivalDTO> festivals = festivalService.getAll();

                log.info("DB 축제 개수 = {}", festivals.size());

                // 축제명 검색
                List<FestivalDTO> matchedFestivals = festivals.stream()
                                .filter(festival ->
                                        festival.getTitle() != null
                                                && searchQuestion.contains(
                                                festival.getTitle()
                                        )
                                )
                                .limit(5)
                                .toList();
                log.info("축제명 검색 결과 = {}", matchedFestivals.size());

                // 축제명이 없으면 지역 검색
                if (matchedFestivals.isEmpty()) {
                    matchedFestivals = findFestivalsByRegion(
                                    festivals,
                                    searchQuestion
                            );
                    log.info("지역 검색 결과 = {}", matchedFestivals.size());
                }

                // 검색 결과가 없는 경우
                if (matchedFestivals.isEmpty()) {
                    prompt = """
                            
                            사용자의 질문에 답변하세요.

                            [사용자 질문]
                            %s

                            해당 조건에 맞는 등록된 축제 정보를 찾을 수 없습니다.

                            규칙:
                            - 존재하지 않는 축제 정보를 만들지 마세요.
                            - 등록된 축제 정보가 없다고 안내하세요.
                            - 한국어로 답변하세요.
                            
                            """.formatted(question);
                    break;
                }

                // 축제 정보 문자열 생성
                StringBuilder festivalInfo = new StringBuilder();

                for (FestivalDTO festival : matchedFestivals) {
                    festivalInfo.append("""
                            축제명 : %s
                            기간 : %s ~ %s
                            장소 : %s
                            주소 : %s
                            홈페이지 : %s
                            진행상태 : %s
                            
                            """.formatted(
                            festival.getTitle(),
                            festival.getStartDate(),
                            festival.getEndDate(),
                            festival.getEventPlace(),
                            festival.getAddress(),
                            festival.getOfficialHomepage(),
                            festival.getStatus()
                    ));
                }

                // FESTIVAL Prompt
                prompt = """
                        
                        [System Instruction]
                        사용자의 질문에 답변하세요.

                        [사용자 질문]
                        %s

                        [DB에 등록된 축제 정보]
                        %s

                        [답변 규칙]
                        - 반드시 DB에 제공된 정보만 사용하세요.
                        - DB에 없는 축제 정보를 만들지 마세요.
                        - 축제명, 기간, 장소, 주소를 알려주세요.
                        - 진행상태가 있으면 알려주세요.
                        - 여러 축제가 있으면 각각 구분해서 보여주세요.
                        - 정보가 없는 항목은 추측하지 마세요.
                        - 생각 과정이나 혼잣말을 출력하지 마세요.
                        - 완성된 한국어 답변만 출력하세요.
                        
                        """.formatted(
                        question,
                        festivalInfo
                );
                break;

            // JOB
            case JOB:
                // 전체 모집공고 조회
                List<Recruitment> allRecruitments = recruitmentRepository.findAll();

                log.info("전체 모집공고 개수 = {}", allRecruitments.size());

                // 삭제되지 않은 모집공고만 검색
                List<Recruitment> activeRecruitments = allRecruitments.stream()
                        .filter(recruitment ->
                                recruitment.getDeletedAt() == null
                        )
                        .toList();
                log.info("삭제되지 않은 모집공고 개수 = {}", activeRecruitments.size());

                // 지역으로 알바 검색
                List<Recruitment> matchedRecruitments = activeRecruitments.stream()
                                .filter(recruitment -> {

                                    // 축제 정보가 없으면 제외
                                    if (recruitment.getFestival() == null) {
                                        return false;
                                    }
                                    // 축제 주소 가져오기
                                    String address = recruitment.getFestival().getAddress();

                                    if (address == null || address.isBlank()) {
                                        return false;
                                    }

                                    // 사용자의 질문에 주소가 포함되어 있는지 확인
                                    String[] addressParts = address.split("\\s+");
                                    for (String part : addressParts) {

                                        if (part.length() < 2) {
                                            continue;
                                        }
                                        // 질문에 주소의 지역명이 포함되어 있으면 검색
                                        if (searchQuestion.contains(part)) {
                                            return true;
                                        }
                                        String normalizedPart = normalizeRegionName(part);

                                        if (normalizedPart != null && searchQuestion.contains(normalizedPart)) {
                                            return true;
                                        }
                                    }
                                    return false;
                                })
                                .limit(5)
                                .toList();
                log.info("서울 지역 모집공고 개수 = {}", matchedRecruitments.size());

                // 알바가 없는 경우
                if (matchedRecruitments.isEmpty()) {
                    prompt = """
                사용자의 질문에 답변하세요.

                [사용자 질문]
                %s

                해당 지역에 등록된 알바 정보가 없습니다.

                규칙:
                - 존재하지 않는 알바 정보를 만들지 마세요.
                - 등록된 알바 정보가 없다고 안내하세요.
                - 한국어로 답변하세요.
                """.formatted(question);

                    break;
                }



                // 알바 정보 문자열 생성
                StringBuilder jobInfo = new StringBuilder();

                for (Recruitment recruitment : matchedRecruitments) {
                    jobInfo.append("""
                        축제: %s
                        모집공고: %s
                        내용: %s
                        모집인원: %s명
                        근무기간: %s ~ %s
                        근무시간: %s ~ %s
                        모집마감: %s
                        상태: %s
                        
                        """.formatted(
                            recruitment.getFestival().getTitle(),
                            recruitment.getTitle(),
                            recruitment.getContent(),
                            recruitment.getRecruitmentCount(),
                            recruitment.getWorkingStartDate(),
                            recruitment.getWorkingEndDate(),
                            recruitment.getWorkingStartTime(),
                            recruitment.getWorkingEndTime(),
                            recruitment.getApplicationDeadline(),
                            recruitment.getStatus()
                    ));
                }

                log.info("JOB 정보 = \n{}", jobInfo);

                // JOB Prompt
                prompt = """
                    [System Instruction]
                    사용자의 질문에 답변하세요.
            
                    [사용자 질문]
                    %s
            
                    [축제 알바 정보]
                    %s
            
                    [답변 형식]
                    반드시 아래 형식을 그대로 사용하세요.
            
                    축제: 축제명
                    모집공고: 모집공고명
                    내용: 내용
                    모집인원: 모집인원명
                    근무기간: 시작일 ~ 종료일
                    모집마감: 마감일
                    상태: 상태
            
                    [답변 규칙]
                    - 반드시 제공된 알바 정보만 사용하세요.
                    - 존재하지 않는 알바 정보를 만들지 마세요.
                    - 검색된 모든 알바 정보를 보여주세요.
                    - 각각의 항목은 반드시 새로운 줄에 작성하세요.
                    - 항목 사이에 '|' 기호를 사용하지 마세요.
                    - 한 줄에 여러 항목을 작성하지 마세요.
                    - 축제 이름을 반드시 보여주세요.
                    - 모집공고를 반드시 보여주세요.
                    - 내용을 반드시 보여주세요.
                    - 모집인원을 반드시 보여주세요.
                    - 근무기간을 반드시 보여주세요.
                    - 모집마감을 반드시 보여주세요.
                    - 상태를 반드시 보여주세요.
                    - 여러 개의 알바가 있으면 공고와 공고 사이에 빈 줄을 하나 넣으세요.
                    - 생각 과정이나 혼잣말을 출력하지 마세요.
                    - 완성된 한국어 답변만 출력하세요.
                    
                    """.formatted(
                        question,
                        jobInfo
                );
                break;

            // ETC
            case ETC:
                prompt = "죄송합니다. 축제 및 축제 알바 관련 질문만 답변할 수 있습니다.";
                break;
        }

        // Groq System Message
        MessageDTO system = new MessageDTO(
                        "system",
                        "너는 축제 정보를 안내하는 AI 챗봇이다. "
                                + "제공된 축제 정보를 우선적으로 사용해서 답변하고, "
                                + "제공된 정보에 없는 내용은 임의로 만들어내지 마라."
                );

        // User Message
        MessageDTO user = new MessageDTO(
                        "user",
                        prompt
                );

        // Groq 요청 DTO 생성
        ChatRequestDTO request = new ChatRequestDTO(model, List.of(system, user));

        // Groq API 호출
        ChatResponseDTO response;
        try {
            response = webClient.post()
                            .uri(apiUrl)
                            .header(
                                    "Authorization",
                                    "Bearer " + apiKey
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .bodyValue(request)
                            .retrieve()
                            .bodyToMono(
                                    ChatResponseDTO.class
                            )
                            .block();
        } catch (Exception e) {
            log.error("Groq API 호출 중 오류 발생", e);
            throw e;
        }

        // AI 답변 가져오기
        String answer = response
                        .getChoices()
                        .get(0)
                        .getMessage()
                        .getContent();

        log.info("AI 답변 = {}", answer);

        // 채팅 저장
        if (memberId != null) {
            chatHistoryService.saveMemberChat(memberId, question, answer);
        } else {
            chatHistoryService.saveGuestChat(sessionId, question, answer);
        }
        return response;
    }

    // 지역으로 축제 검색
    private List<FestivalDTO> findFestivalsByRegion(List<FestivalDTO> festivals, String question) {

        List<FestivalDTO> result = new ArrayList<>();

        for (FestivalDTO festival : festivals) {

            String address = festival.getAddress();

            // 주소가 없으면 검색하지 않음
            if (address == null || address.isBlank()) {
                continue;
            }

            String[] addressParts = address.split("\\s+");

            for (String part : addressParts) {

                if (part.length() < 2) {
                    continue;
                }

                if (question.contains(part)) {
                    result.add(festival);
                    break;
                }

                String normalizedPart = normalizeRegionName(part);

                if (normalizedPart != null && question.contains(normalizedPart)) {
                    result.add(festival);
                    break;
                }
            }
        }
        // 최대 5개만 반환
        return result.stream()
                .limit(5)
                .toList();
    }

    // 행정구역 이름 정규화
    private String normalizeRegionName(String region) {

        if (region == null || region.isBlank()) {
            return null;
        }
        if (region.endsWith("특별자치도")) {
            return region.substring(0, region.length() - 5);
        }

        if (region.endsWith("특별자치시")) {
            return region.substring(0, region.length() - 5);
        }

        if (region.endsWith("광역시")) {
            return region.substring(0, region.length() - 3);
        }

        if (region.endsWith("특별시")) {
            return region.substring(0, region.length() - 3);
        }

        if (region.endsWith("시")) {
            return region.substring(0, region.length() - 1);
        }

        if (region.endsWith("군")) {
            return region.substring(0, region.length() - 1);
        }

        if (region.endsWith("구")) {
            return region.substring(0, region.length() - 1);
        }
        return null;
    }

    // 이전 질문에서 축제명 찾기
    private String extractFestivalTitle(
            String question
    ) {
        if (question == null || question.isBlank()) {
            return null;
        }
        List<FestivalDTO> festivals = festivalService.getAll();
        for (FestivalDTO festival : festivals) {
            if (festival.getTitle() != null && question.contains(festival.getTitle())) {
                return festival.getTitle();
            }
        }
        return null;
    }
}