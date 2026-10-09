package com.velora.backend.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MailService {

    private static final String RESEND_URL = "https://api.resend.com/emails";
    private static final String FROM = "Velora <onboarding@resend.dev>";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendMail(String to, String subject, String body) {

        String apiKey = System.getenv("RESEND_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("RESEND_API_KEY is not set");
        }

        try {
            String json = objectMapper.writeValueAsString(Map.of(
                    "from", FROM,
                    "to", new String[] { to },
                    "subject", subject,
                    "text", body
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(RESEND_URL))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 300) {
                throw new RuntimeException("Resend failed: "
                        + response.statusCode() + " " + response.body());
            }

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void sendOtpMail(String email, String otp) {
        sendMail(
                email,
                "Velora Password Reset OTP",
                "Your OTP is: " + otp + "\n\nThis OTP is valid for 5 minutes."
        );
    }
}