package com.example.chook.chatbot;

import org.springframework.stereotype.Component;

@Component
public class QuestionClassifier {
    public QuestionType classify(String question) {

        if (question.contains("알바")
                || question.contains("아르바이트")
                || question.contains("채용")
                || question.contains("모집")
                || question.contains("스태프")
                || question.contains("시급")
                || question.contains("급여")
                || question.contains("월급")
                || question.contains("일급")
                || question.contains("작업")) {

            return QuestionType.JOB;
            // 사용자가 입력한 문장에 특정 단어가 있는지 확인한다. => 결과가 참(true) 이면 => "이 질문은 JOB 관련 질문이다."
        }

        if (question.contains("축제")
                || question.contains("페스티벌")
                || question.contains("행사")
                || question.contains("공연")
                || question.contains("일정")
                || question.contains("부스")) {

            return QuestionType.FESTIVAL;
            // 사용자가 입력한 문장에 특정 단어가 있는지 확인한다. => 결과가 참(true) 이면 => "이 질문은 FESTIVAL 관련 질문이다."
        }

        return QuestionType.ETC;
    }
}
