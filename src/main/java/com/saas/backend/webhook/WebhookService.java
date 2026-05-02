package com.saas.backend.webhook;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.saas.backend.util.SignatureUtil;
import org.springframework.scheduling.annotation.Async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookService {

    @Autowired
    private WebhookRepository webhookRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    // 🔥 Step 1: Save event
    public void triggerWebhook(String url, String payload) {

        Webhook webhook = Webhook.builder()
            .url(url)
            .payload(payload)
            .status("PENDING")
            .retryCount(0)
            .secret("my-secret-key")   // 🔥 ADD THIS
            .build();
         webhookRepository.save(webhook);

        // 🔥 Step 2: Send immediately (for now)
        sendWebhook(webhook);
    }

    // 🔥 Step 2: Send request
    @Async
public void sendWebhook(Webhook webhook) {

    webhook.setStatus("PROCESSING");
    webhookRepository.save(webhook);

    try {
        System.out.println("🚀 Sending webhook ID: " + webhook.getId());

        String payload = webhook.getPayload();

        String signature = SignatureUtil.generateHmac(
                payload,
                webhook.getSecret() != null ? webhook.getSecret() : "default-secret"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Signature", signature);

        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        restTemplate.postForEntity(
                webhook.getUrl(),
                request,
                String.class
        );

        webhook.setStatus("SUCCESS");
        System.out.println("✅ Webhook SUCCESS: " + webhook.getId());

    } catch (Exception e) {
        webhook.setStatus("FAILED");
        webhook.setRetryCount(webhook.getRetryCount() + 1);
        long backoff = (long) Math.pow(2, webhook.getRetryCount()) * 60 * 1000;
        webhook.setNextRetryTime(System.currentTimeMillis() + backoff);

        System.out.println("❌ Webhook FAILED: " + webhook.getId());
    }

    webhookRepository.save(webhook);
}
}