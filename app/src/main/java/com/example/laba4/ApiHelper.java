package com.example.laba4;


import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ApiHelper {
    private static final String API_URL = "https://media.ifmo.ru/api_get_current_song.php";
    private static final String LOGIN = "4707login";
    private static final String PASSWORD = "4707pass";

    public static String fetchCurrentTrack() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);

            String params = "login=" + URLEncoder.encode(LOGIN, "UTF-8") +
                    "&password=" + URLEncoder.encode(PASSWORD, "UTF-8");

            try (OutputStream os = connection.getOutputStream()) {
                os.write(params.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            try (Scanner scanner = new Scanner(connection.getInputStream())) {
                StringBuilder response = new StringBuilder();
                while (scanner.hasNextLine()) {
                    response.append(scanner.nextLine());
                }
                return response.toString();
            }
        } catch (Exception e) {
            return "{\"result\":\"error\", \"info\":\"" + e.getMessage() + "\"}";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

}
}
