package com.group3.backend.constants;

import java.time.Duration;

public class PaymentConstants {
    // Payment deadline duration (48 hours)
    public static final Duration PAYMENT_DEADLINE_DURATION = Duration.ofHours(48);

    // Private constructor to prevent instantiation
    private PaymentConstants() {}
}
