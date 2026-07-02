package JHS;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class weatherPoemApp {
    public static void main(String[] args){

        System.out.println("[1단계] 기상청에서 오늘의 실시간 날씨를 조회합니다...");
        String weatherJson = weatherService.getWeatherData();

        // 데이터 기본값 설정 (파싱 실패 대비 백업 데이터)
        double currentTemp = 15.0;
        String rainStatus = "맑거나 흐린 하늘";

        if (weatherJson != null && !weatherJson.isEmpty()) {
            try {
                // 1. 트리모델 파싱 시작
                JsonObject rootObj = JsonParser.parseString(weatherJson).getAsJsonObject();
                JsonObject response = rootObj.getAsJsonObject("response");
                JsonObject body = response.getAsJsonObject("body");
                JsonObject items = body.getAsJsonObject("items");
                JsonArray itemArray = items.getAsJsonArray("item"); // 날씨 요소들이 들어있는 배열

                // 2. 배열 루프를 돌며 필요한 카테고리만 매칭
                for (JsonElement element : itemArray) {
                    JsonObject item = element.getAsJsonObject();
                    String category = item.get("category").getAsString();     // T1H, PTY 등
                    String obsrValue = item.get("obsrValue").getAsString();   // 관측 값

                    // 기온 파싱
                    if ("T1H".equals(category)) {
                        currentTemp = Double.parseDouble(obsrValue);
                    }
                    // 강수 상태 파싱 후 감성 텍스트 매핑
                    else if ("PTY".equals(category)) {
                        int ptyCode = Integer.parseInt(obsrValue);
                        switch (ptyCode) {
                            case 0: rainStatus = "비나 눈 없이 무던한 하늘"; break;
                            case 1: rainStatus = "눈물처럼 촉촉히 내리는 비"; break;
                            case 2: rainStatus = "비와 눈이 서글프게 섞여 내리는 진눈깨비"; break;
                            case 3: rainStatus = "포근하게 온 세상을 덮어주는 하얀 눈"; break;
                            case 5: rainStatus = "하늘에서 가만히 떨어지는 은은한 빗방울"; break;
                            case 6: rainStatus = "진눈깨비가 가볍게 흩날리는 날씨"; break;
                            case 7: rainStatus = "하늘하늘 춤추며 가볍게 날리는 눈발"; break;
                            default: rainStatus = "예측할 수 없는 묘한 하늘"; break;
                        }
                    }
                }
                System.out.println(String.format("   -> 파싱 성공! 현재 기온: %.1f도, 상태: %s", currentTemp, rainStatus));

            } catch (Exception e) {
                System.out.println("기상청 데이터 파싱 실패! (공공 API 포털의 점검 혹은 키 인코딩 오류일 수 있습니다.)");
                System.out.println("   -> 기본 예시 데이터로 진행합니다.");
                //실패 대비 기본데이터 입력
                currentTemp = 21.3;
                rainStatus = "선선하고 기분 좋은 바람이 부는 날씨";
            }
        } else {
            System.out.println("기상청 데이터를 가져오지 못했습니다. 프로그램을 종료합니다.");
            return;
        }

        System.out.println("[2단계] 추출된 날씨로 제미나이 전용 프롬프트를 조립합니다...");
        String prompt = weatherOrchestration.createPrompt(currentTemp, rainStatus);

        System.out.println("[3단계] 제미나이 AI에게 시 작성을 요청하고 결과를 정제하는 중...");
        //구글 서버가 한도 초과(429)로 차단하는 것을 막기 위해 2.5초간 강제 휴식
        try {
            Thread.sleep(2500); // 2500 밀리초 = 2.5초
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String finalPoem = geminiService.generatePoem(prompt);

        //최종 결과 출력
        System.out.println("\n=======================================================");
        System.out.println("제미나이가 당신의 날씨를 보고 지은 오늘의 시");
        System.out.println("=======================================================");
        System.out.println(finalPoem);
        System.out.println("=======================================================");

    }
}
