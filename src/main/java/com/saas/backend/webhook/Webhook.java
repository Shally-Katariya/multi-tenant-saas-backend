package com.saas.backend.webhook;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Webhook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private String status; // PENDING, SUCCESS, FAILED

    private int retryCount;

    @Column(name = "next_retry_time")
    private long nextRetryTime = 0;

    private String secret;
}