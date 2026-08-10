package com.example.chook.payment.service;
// 토스페이먼츠 깃허브 샘플 프로젝트 원본 로직 - HTTP 통신 부분만 그대로 옮김
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
//토스랑 통신 담당
@Slf4j
@Component
public class TossPaymentApiClient {

    //문자열형태의 json을 받아서
    //문자열을 코드에서 다룰수있는 json object로 변환.
    //파싱 실패하면 빈객체
    public JSONObject parseRequestData(String jsonBody) {
        try {
            return (JSONObject) new JSONParser().parse(jsonBody);
        } catch (ParseException e) {
            log.error("JSON Parsing Error", e);
            return new JSONObject();
        }
    }

    //토스한테 보낼 데이터    인증에 쓸 시크릿키    토스api주소를 받아서
    //createConnection으로 연결 만들고 requestdata를 연결에 실제로 전송 POST 토스가 준 응답 다시 읽음.
    //토스 서버가 돌려준 응답의 승인 성공 실패 결과를 담음.
    public JSONObject sendRequest(JSONObject requestData, String secretKey, String urlString) throws IOException {
        HttpURLConnection connection = createConnection(secretKey, urlString);
        try (OutputStream os = connection.getOutputStream()) {
            os.write(requestData.toString().getBytes(StandardCharsets.UTF_8));
        }

        try (InputStream responseStream = connection.getResponseCode() == 200 ? connection.getInputStream() : connection.getErrorStream();
             Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
            return (JSONObject) new JSONParser().parse(reader);
        } catch (Exception e) {
            log.error("Error reading response", e);
            JSONObject errorResponse = new JSONObject();
            errorResponse.put("error", "Error reading response");
            return errorResponse;
        }
    }

    //시크릿키랑 접속할 주소를 받음.
    //http 연결 만들어서 인증셋팅
    //연결만 준비된 http url connection 객체를 돌려줌.
    private HttpURLConnection createConnection(String secretKey, String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8)));
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        return connection;
    }
}

//토스랑 어떻게 대화하는지만 알고 누가 뭘 결제해서 db에 뭘 넣을지 같은건 하나도 모름.