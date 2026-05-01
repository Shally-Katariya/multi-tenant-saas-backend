package com.saas.backend.webhook;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    @Autowired
    private WebhookRepository webhookRepository;

    @Autowired
    private WebhookService webhookService;

    // 🔁 Manual retry
    @PostMapping("/retry/{id}")
    public String retryWebhook(@PathVariable Long id) {

        Webhook webhook = webhookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Webhook not found"));

        // 🚫 Block retry if permanently failed
        if ("PERMANENT_FAILED".equals(webhook.getStatus())) {
            return "Webhook " + id + " cannot be retried (permanent failure)";
        }

        webhook.setStatus("FAILED");
        webhook.setNextRetryTime(System.currentTimeMillis());

        webhookRepository.save(webhook);

        webhookService.sendWebhook(webhook);

        return "Webhook " + id + " retry triggered successfully";
    }

    // 📊 Get ALL webhooks
    @GetMapping
    public List<Webhook> getAllWebhooks() {
        return webhookRepository.findAll();
    }

    // ❌ Get FAILED webhooks (optimized query)
    @GetMapping("/failed")
    public List<Webhook> getFailedWebhooks() {
        return webhookRepository.findByStatus("FAILED");
    }

    // ✅ Get SUCCESS webhooks
    @GetMapping("/success")
    public List<Webhook> getSuccessfulWebhooks() {
        return webhookRepository.findByStatus("SUCCESS");
    }

    // 🔍 Get webhook by ID
    @GetMapping("/{id}")
    public Webhook getWebhookById(@PathVariable Long id) {
        return webhookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Webhook not found"));
    }

    @PostMapping("/test")
    public String testWebhook() {

    String url = "https://invalid-url-1234.com/test";

    String payload = "{\"message\":\"Test Success\"}";

    webhookService.triggerWebhook(url, payload);

    return "Triggered";
}
}