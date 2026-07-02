package JHS;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class weatherService {
    public static String getWeatherData(){
        try {
            String baseDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            // 초단기실황 안전을 위해 1시간 전 데이터 조회하기
            String baseTime = LocalTime.now().minusHours(1).format(DateTimeFormatter.ofPattern("HH")) + "00";

            String serviceKey = "8846d85ea6ffd25f0886b8a279b60b26900abdf929f805963497eeb275264ebd";
            String baseUrl = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0";

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
