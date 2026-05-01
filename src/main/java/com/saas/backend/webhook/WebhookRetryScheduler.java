package com.saas.backend.webhook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WebhookRetryScheduler {

    @Autowired
    private WebhookRepository webhookRepository;

    @Autowired
    private WebhookService webhookService;

    @Scheduled(fixedRate = 10000)
public void retryFailedWebhooks() {

    long now = System.currentTimeMillis();

    List<Webhook> webhooks =
        webhookRepository.findByStatusAndNextRetryTimeLessThanEqual("FAILED", now);

    for (Webhook webhook : webhooks) {

        if (webhook.getRetryCount() >= 3) {
            webhook.setStatus("PERMANENT_FAILED");
            webhookRepository.save(webhook);
            continue;
        }

        System.out.println("🔁 Retrying webhook ID: " + webhook.getId());

        webhookService.sendWebhook(webhook);
    }
}
}