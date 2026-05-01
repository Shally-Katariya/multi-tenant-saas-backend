package com.saas.backend.webhook;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WebhookRepository extends JpaRepository<Webhook, Long> {

    List<Webhook> findByStatus(String status);

    List<Webhook> findByStatusAndNextRetryTimeLessThanEqual(String status, long time);
}