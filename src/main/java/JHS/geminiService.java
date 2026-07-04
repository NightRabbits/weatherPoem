package JHS;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class geminiService {
    // 환경 변수에서 안전하게 키를 로드함
    private static final String GEMINI_API_KEY = System.getenv("GEMINI_API_KEY");

    public static String generatePoem(String promptText){
        //환경 변수가 비어있는지 먼저 체크
        if (GEMINI_API_KEY == null || GEMINI_API_KEY.isEmpty()) {
            System.out.println("[에러] GEMINI_API_KEY 환경 변수가 설정되지 않았습니다.");
            return "API 키 설정 오류로 시를 지을 수 없습니다.";
        }

        int maxRetries = 3;    // 최대 재시도 횟수
        int retryDelay = 30000; // 에러 발생 시 대기 시간 (30초)

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                String urlStr = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=" + GEMINI_API_KEY;
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // JSON 요청 데이터 조립
                JsonObject textObj = new JsonObject();
                textObj.addProperty("text", promptText);
                com.google.gson.JsonArray partsArray = new com.google.gson.JsonArray();
                partsArray.add(textObj);
                JsonObject contentObj = new JsonObject();
                contentObj.add("parts", partsArray);
                com.google.gson.JsonArray contentsArray = new com.google.gson.JsonArray();
                contentsArray.add(contentObj);
                JsonObject requestJson = new JsonObject();
                requestJson.add("contents", contentsArray);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = requestJson.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // 응답 코드 확인
                int responseCode = conn.getResponseCode();

                // 429 한도 초과 에러 발생 시 구글의 에러메세지 출력
                if (responseCode == 429) {
                    System.out.println("\n-------------------------------------------------------");
                    System.out.println("구글 서버가 요청을 거절했습니다(429). 상세 원인을 분석합니다...");

                    // 에러 스트림(Error Stream)을 열어 구글이 보낸 JSON 에러 메시지를 읽어옴
                    try (BufferedReader eb = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
                        StringBuilder errorLog = new StringBuilder();
                        String errorLine;
                        while ((errorLine = eb.readLine()) != null) {
                            errorLog.append(errorLine.trim());
                        }
                        // 콘솔창에 구글이 보낸 날것의 에러 텍스트를 출력
                        System.out.println("[구글 서버 에러 API 답변]: " + errorLog.toString());
                    } catch (Exception ignored) {}
                    System.out.println("-------------------------------------------------------\n");

                    System.out.println("[경고] 구글 API 요청 한도 초과(429). " + (retryDelay / 1000) + "초 후 자동으로 다시 시도합니다... (" + attempt + "/" + maxRetries + ")");
                    Thread.sleep(retryDelay);
                    continue; // 다음 루프로 이동하여 재시도
                }

                // 정상 응답 받기 (200 OK 등)
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                br.close();

                // JSON 파싱하여 시 본문 추출
                JsonObject rawResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
                String poemResult = rawResponse.getAsJsonArray("candidates")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("content")
                        .getAsJsonArray("parts")
                        .get(0).getAsJsonObject()
                        .get("text").getAsString();

                return poemResult; // 성공 시 시 본문 반환

            } catch (Exception e) {
                // 마지막 시도마저 실패했을 때만 에러 로그를 출력[cite: 5]
                if (attempt == maxRetries) {
                    e.printStackTrace();
                    return "시를 짓는 도중 오류가 발생했습니다.";
                }
                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return "구글 서버 한도 초과로 인해 시를 가져오지 못했습니다. 잠시 후 다시 실행해 주세요.";
    }
}
