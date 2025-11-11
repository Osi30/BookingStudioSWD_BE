package com.studio.booking.services.impl;

import com.studio.booking.configs.MomoConfig;
import com.studio.booking.entities.Payment;
import com.studio.booking.exceptions.exceptions.PaymentException;
import com.studio.booking.services.MomoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MomoServiceImpl implements MomoService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${MOMO_PARTNER_CODE}")
    private String partnerCode;

    @Value("${MOMO_ACCESS_KEY}")
    private String accessKey;

    @Value("${MOMO_SECRET_KEY}")
    private String secretKey;

    @Value("${MOMO_ENDPOINT}")
    private String endpoint;

    @Value("${MOMO_REDIRECT_URL}")
    private String redirectUrl;

    @Value("${MOMO_IPN_URL}")
    private String ipnUrl;

    @Value("${MOMO_REQUEST_TYPE}")
    private String requestType;

    @Override
    public String createMomoUrl(Payment payment) throws NoSuchAlgorithmException, InvalidKeyException {
        // Create signature
        String requestId = partnerCode + System.currentTimeMillis();
        String rawSignature = buildRawSignature(requestId, payment);
        String signature = MomoConfig.createHmacSha256(rawSignature, secretKey);

        // Prepare request body
        Map<String, Object> requestBody = getStringObjectMap(payment, requestId, signature);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // Make request to Momo
        ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, entity, Map.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new PaymentException("Failed to create Momo payment: " + response.getStatusCode());
        }

        Map responseBody = response.getBody();
        return responseBody.get("payUrl") != null ? String.valueOf(responseBody.get("payUrl")) : null;
    }

    private Map<String, Object> getStringObjectMap(Payment payment, String requestId, String signature) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("accessKey", accessKey);
        requestBody.put("amount", payment.getAmount().longValue());
        requestBody.put("extraData", "NoExtraData");
        requestBody.put("ipnUrl", ipnUrl);
        requestBody.put("orderId", payment.getId());
        requestBody.put("orderInfo", "Thanh toán đơn hàng");
        requestBody.put("partnerCode", partnerCode);
        requestBody.put("redirectUrl", redirectUrl);
        requestBody.put("requestId", requestId);
        requestBody.put("requestType", requestType);
        requestBody.put("lang", "vi");
        requestBody.put("signature", signature);
        return requestBody;
    }

    private String buildRawSignature(String requestId, Payment payment) {
        return "accessKey=" + accessKey +
                "&amount=" + payment.getAmount().longValue() +
                "&extraData=" + "NoExtraData" +
                "&ipnUrl=" + ipnUrl +
                "&orderId=" + payment.getId() +
                "&orderInfo=" + "Thanh toán đơn hàng" +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId +
                "&requestType=" + requestType;
    }
}
