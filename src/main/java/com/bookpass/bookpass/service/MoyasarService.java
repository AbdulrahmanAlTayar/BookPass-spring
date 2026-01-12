package com.bookpass.bookpass.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MoyasarService {

    @Value("${moyasar.secret-key}")
    private String secretKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public void verifyPayment(String paymentId, BigDecimal amount) {
        String url = "https://api.moyasar.com/v1/payments/" + paymentId;

        HttpHeaders headers = new HttpHeaders();
        // Basic Auth: username=secretKey, password=""
        String auth = secretKey + ":";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.set("Authorization", "Basic " + encodedAuth);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> body = response.getBody();

            if (body == null) {
                throw new RuntimeException("Empty response from Moyasar");
            }

            // 1. Check Status
            String status = (String) body.get("status");
            if (!"paid".equals(status)) {
                throw new RuntimeException("Payment not paid. Status: " + status);
            }

            // 2. Check Amount (Moyasar uses Halalas/integers)
            // Example: 50.00 SAR -> 5000 halalas
            Integer moyasarAmount = (Integer) body.get("amount");
            BigDecimal expectedAmount = amount.multiply(BigDecimal.valueOf(100)); // Convert SAR to Halalas
            
            // Compare as integers to avoid precision issues
            if (moyasarAmount.compareTo(expectedAmount.intValue()) != 0) {
                 throw new RuntimeException("Payment amount mismatch. Expected: " + expectedAmount.intValue() + ", Got: " + moyasarAmount);
            }

            log.info("Payment verified successfully: {}", paymentId);

        } catch (Exception e) {
            log.error("Moyasar verification failed", e);
            throw new RuntimeException("Payment verification failed: " + e.getMessage());
        }
    }
}
