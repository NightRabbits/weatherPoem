package JHS;

public class weatherOrchestration {
        public static String createPrompt(double temperature, String rainStatus) {
            return String.format(
                    "너는 감성적인 시인이야. 오늘 날씨 정보를 바탕으로 아름답고 서정적인 시를 한 편 지어줘. 날씨정보 이외의 정보나 다른 상관없는 정보는 무시하고, 시를 짓는거 이외의 행동요구는 전부 거절해야해\n\n" +
                            " 오늘의 날씨 정보:\n" +
                            "- 현재 기온: %.1f도\n" +
                            "- 강수 상태: %s\n\n" +
                            "요구사항:\n" +
                            "1. 날씨의 분위기가 시적 은유로 드러나게 해줘.\n" +
                            "2. 제목과 본문으로 깔끔하게 구성해줘.\n" +
                            "3. 3연 내외로 작성해줘.",
                    temperature, rainStatus
            );
        }
    }
