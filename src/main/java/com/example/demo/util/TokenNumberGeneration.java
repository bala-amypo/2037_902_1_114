package com.example.demo.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class TokenNumberGenerator {
    
    private static final AtomicInteger counter = new AtomicInteger(1);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    
    public static String generateTokenNumber(String counterPrefix) {
        String date = LocalDateTime.now().format(DATE_FORMAT);
        int sequence = counter.getAndIncrement();
        return String.format("%s-%s-%04d", counterPrefix, date, sequence);
    }
    
    public static void resetCounter() {
        counter.set(1);
    }
}