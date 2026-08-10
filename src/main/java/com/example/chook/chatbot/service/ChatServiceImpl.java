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
public class ChatServiceImpl implements ChatService{

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
        String question =
                chatRequestDTO.getMessages()
                        .get(chatRequestDTO.getMessages().size() - 1)
                        .getContent();

        // 이전 대화 조회
        List<ChatHistory> historyList = chatHistoryService.getLastChat(sessionId);

        String festivalTitle = null;

        if (historyList != null && !historyList.isEmpty()){

            for (ChatHistory history : historyList){

                log.info("이전 질문 : {}", history.getQuestion());
                log.info("이전 답변 : {}", history.getAnswer());

                festivalTitle = extractFestivalTitle(history.getQuestion());

                // 축제명을 찾으면 반복 종료
                if (festivalTitle != null){
                    break;
                }
            }
            log.info("이전 대화에서 찾은 축제명 : {}", festivalTitle);
        }

        // 질문 분류하기
        QuestionType questionType = questionClassifier.classify(question);

        log.info("질문 : {}", question);
        log.info("분류 결과 : {}", questionType);

        if (questionType == QuestionType.JOB && festivalTitle != null){

            if (question.contains("여기")
                    || question.contains("거기")
                    || question.contains("그 축제")
                    || question.contains("시급")
                    || question.contains("일급")
                    || question.contains("급여")) {

                question = festivalTitle + " " + question.replace("여기", "");
            }
        }

        log.info("질문 : {}", question);
        log.info("분류 결과 : {}", questionType);

        // Groq에게 전달할 내용
        String prompt = question;
        String searchQuestion  = question;

        switch (questionType) {
            // 축제질문
            case FESTIVAL:
                // DB에서 축제 정보 조회
                List<FestivalDTO> festivals = festivalService.getAll();

                log.info("축제 개수 = {}", festivals.size());
                log.info("축제 목록 = {}", festivals);

                if(festivals.isEmpty()){
                    prompt = "등록된 축제 정보를 찾을 수 없습니다.";
                    break;
                }

                // 축제명 검색
                List<FestivalDTO> matchedFestivals = festivals.stream()
                        .filter(festival ->
                                festival.getTitle() != null
                                        && searchQuestion.contains(festival.getTitle())
                        )
                        .toList();

                // 특정 축제명이 없으면 지역검색
                String region = null;
                String[] regions = {
                        "서울", "부산", "대구", "인천",
                        "광주", "대전", "울산", "세종",
                        "경기", "강원", "충북", "충남",
                        "전북", "전남", "경북", "경남",
                        "제주"
                };
                for (String r : regions) {
                    if (question.contains(r)) {
                        region = r;
                        break;
                    }
                }

                // 지역명을 기준으로 검색
                if (matchedFestivals.isEmpty() && region != null) {

                    String searchRegion = region;

                    matchedFestivals = festivals.stream()
                            .filter(festival ->
                                    festival.getAddress() != null
                                            && festival.getAddress().contains(searchRegion)
                            )
                            .toList();
                }

                // 검색결과 없으면 전체 축제 조회
                if (matchedFestivals.isEmpty()) {
                    matchedFestivals = festivals;
                }

                // 축제 정보를 문자열로 만들기
                StringBuilder festivalInfo = new StringBuilder();
                for (FestivalDTO festival : matchedFestivals){
                    festivalInfo.append("""
                        제목 : %s
                        일정 : %s ~ %s
                        장소 : %s
                        주소 : %s
                            
                        """.formatted(
                                festival.getTitle(),
                                festival.getStartDate(),
                                festival.getEndDate(),
                                festival.getEventPlace(),
                                festival.getAddress()
                        ));
                }

                // GTP에게 보낼 프롬프트 생성
                prompt = """
                    사용자의 질문에 답변하세요.
            
                    [사용자 질문]
                    %s
            
                    [축제 정보]
                    %s
                    
                    1. 축제명
                        기간: 시작일 ~ 종료일
                        장소: 장소
                        
                    2. 축제명
                        기간: 시작일 ~ 종료일
                        장소: 장소
                    
                    규칙:
                    - 모든 축제를 빠짐없이 보여주세요.
                    - 축제마다 반드시 줄바꿈하세요.
                    - 축제와 축제 사이에는 빈 줄을 하나 넣으세요.
                    - 제공된 정보만 사용하세요.
                    - 없는 정보는 추측하지 마세요.
                    """.formatted(
                        question,
                        festivalInfo.toString()
                );
                break;

                // 알바 질문 처리
            case JOB:
                // DB전체 축제 조회
                List<FestivalDTO> jobFestivals = festivalService.getAll();

                log.info("전체 축제 개수 = {}", jobFestivals.size());

                for (FestivalDTO festival : jobFestivals) {
                    log.info(
                            "축제명 = {}, 주소 = {}",
                            festival.getTitle(),
                            festival.getAddress()
                    );
                }

                // 질문에 축제명이 있는지 확인
                List<FestivalDTO> matchedJobFestivals =
                        jobFestivals.stream()
                                .filter(festival ->
                                        festival.getTitle() != null
                                                && searchQuestion.contains(
                                                festival.getTitle()
                                        )
                                )
                                .toList();
                // 질문에서 지역 찾기
                String jobRegion = null;
                String[] jobRegions = {
                        "서울", "부산", "대구", "인천",
                        "광주", "대전", "울산", "세종",
                        "경기", "강원", "충북", "충남",
                        "전북", "전남", "경북", "경남",
                        "제주"
                };
                for (String r : jobRegions) {
                    if (question.contains(r)) {
                        jobRegion = r;
                        break;
                    }
                }

                // 축제명이 없고 지역이 있으면
                if (matchedJobFestivals.isEmpty() && jobRegion != null) {

                    String searchRegion = jobRegion;
                    matchedJobFestivals =
                            jobFestivals.stream()
                                    .filter(festival ->
                                            festival.getAddress() != null
                                                    && festival.getAddress()
                                                    .contains(searchRegion)
                                    )
                                    .toList();
                }

                // 축제도 지역도 못 찾은 경우
                if (matchedJobFestivals.isEmpty()) {
                    prompt = "질문에 해당하는 축제 정보를 찾을 수 없습니다.";
                    break;
                }

                // 찾은 여러 축제의 알바를 하나의 리스트에 모두 저장
                List<Recruitment> recruitments = new ArrayList<>();

                for (FestivalDTO festival : matchedJobFestivals) {

                    log.info(
                            "알바 조회 축제 = {}, contentId = {}",
                            festival.getTitle(),
                            festival.getContentId()
                    );

                    List<Recruitment> festivalRecruitments =
                            recruitmentRepository
                                    .findByFestival_ContentId(
                                            festival.getContentId()
                                    );
                    recruitments.addAll(
                            festivalRecruitments
                    );
                }

                // 알바가 하나도 없는 경우
                if (recruitments.isEmpty()) {
                    prompt = "해당 지역 또는 축제에 등록된 알바 정보가 없습니다.";
                    break;
                }

                // 알바 정보 문자열 만들기
                StringBuilder jobInfo = new StringBuilder();

                for (Recruitment recruitment : recruitments) {

                    jobInfo.append("""
                            축제 : %s
                            모집공고 : %s
                            내용 : %s
                            모집인원 : %s
                            근무기간 : %s ~ %s
                            모집마감 : %s
                            상태 : %s

                            """.formatted(
                            recruitment.getFestival().getTitle(),
                            recruitment.getTitle(),
                            recruitment.getContent(),
                            recruitment.getRecruitmentCount(),
                            recruitment.getWorkingStartDate(),
                            recruitment.getWorkingEndDate(),
                            recruitment.getApplicationDeadline(),
                            recruitment.getStatus()
                    ));
                }

                // GPT 프롬프트
                prompt = """
                        사용자의 질문에 답변하세요.

                        [사용자 질문]
                        %s

                        [축제 알바 정보]
                        %s

                        규칙:
                        - 반드시 제공된 알바 정보만 사용하세요.
                        - 존재하지 않는 알바 정보를 만들어내지 마세요.
                        - 검색된 모든 알바 정보를 보여주세요.
                        - 각각의 공고는 줄바꿈하세요.
                        - 축제 이름도 함께 보여주세요.
                        """.formatted(
                        question,
                        jobInfo
                );
                break;

            case ETC:
                prompt = "죄송합니다. 축제 및 축제 알바 관련 질문만 답변할 수 있습니다.";
                break;
        }

        // Groq 요청 메시지 생성
        MessageDTO system = new MessageDTO(
                "system",
                "너는 축제 정보를 안내하는 AI 챗봇이다. " +
                        "제공된 축제 정보를 우선적으로 사용해서 답변하고, " +
                        "제공된 정보에 없는 내용은 임의로 만들어내지 마라."
                );

        // 사용자가 입력한 질문
        MessageDTO user = new MessageDTO(
                "user",
                    prompt
                );

        // Groq 요청 데이터 생성
        ChatRequestDTO request = new ChatRequestDTO(
                        model,
                        List.of(system, user)
                );

        // Groq API 호출
        ChatResponseDTO response = webClient.post()
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
                        .bodyToMono(ChatResponseDTO.class)
                        .block();

        // AI 답변 반환
        String answer = response
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();

        // 로그인 채팅 저장
        if(memberId != null){
            chatHistoryService.saveMemberChat(
                    memberId,
                    question,
                    answer
            );
        // 비로그인 채팅 저장
        }else{
            chatHistoryService.saveGuestChat(
                    sessionId,
                    question,
                    answer
            );
        }
        return response;
    }

    // 이전 질문에서 축제명 찾기
    private String extractFestivalTitle(String question) {

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
