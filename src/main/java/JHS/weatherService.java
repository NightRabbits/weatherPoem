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
            String baseUrl = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";

            StringBuilder urlBuilder = new StringBuilder(baseUrl);

            //기상청 데이터 문자열로 가져오기
            urlBuilder.append("?").append(URLEncoder.encode("serviceKey", "UTF-8")).append("=").append(serviceKey);
            urlBuilder.append("&").append(URLEncoder.encode("pageNo", "UTF-8")).append("=").append(URLEncoder.encode("1", "UTF-8"));
            urlBuilder.append("&").append(URLEncoder.encode("numOfRows", "UTF-8")).append("=").append(URLEncoder.encode("100", "UTF-8"));
            urlBuilder.append("&").append(URLEncoder.encode("dataType", "UTF-8")).append("=").append(URLEncoder.encode("JSON", "UTF-8"));
            urlBuilder.append("&").append(URLEncoder.encode("base_date", "UTF-8")).append("=").append(URLEncoder.encode(baseDate, "UTF-8"));
            urlBuilder.append("&").append(URLEncoder.encode("base_time", "UTF-8")).append("=").append(URLEncoder.encode(baseTime, "UTF-8"));

            //위치 좌표값은 대한민국 서울 기준
            urlBuilder.append("&").append(URLEncoder.encode("nx", "UTF-8")).append("=").append(URLEncoder.encode("60", "UTF-8"));
            urlBuilder.append("&").append(URLEncoder.encode("ny", "UTF-8")).append("=").append(URLEncoder.encode("127", "UTF-8"));

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) ? conn.getInputStream() : conn.getErrorStream(), "UTF-8"
            ));

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            conn.disconnect();

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
