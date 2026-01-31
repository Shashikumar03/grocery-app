package org.example.grocery_app.serviceImplementation;
//
//package com.shashi03.Bazzario.service; // change to your package
//
//import com.shashi03.Bazzario.entity.SimChangeEvent;
//import com.shashi03.Bazzario.repository.SimChangeEventRepository;
import org.example.grocery_app.repository.SimChangeEvent;
import org.example.grocery_app.repository.SimChangeEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Records SIM change events for security audit. Optionally invalidate user tokens/sessions here.
 */
@Service
public class SimChangeService {

    private static final Logger logger = LoggerFactory.getLogger(SimChangeService.class);

    private final SimChangeEventRepository simChangeEventRepository;

    public SimChangeService(SimChangeEventRepository simChangeEventRepository) {
        this.simChangeEventRepository = simChangeEventRepository;
    }

    public void recordSimChange(Long userId, String previousFingerprint, String currentFingerprint) {
        logger.warn("SIM change detected for user id={}, previous={}, current={}",
                userId, maskFingerprint(previousFingerprint), maskFingerprint(currentFingerprint));

        SimChangeEvent event = new SimChangeEvent();
        event.setUserId(userId);
        event.setPreviousFingerprint(previousFingerprint);
        event.setCurrentFingerprint(currentFingerprint);
        event.setDetectedAt(LocalDateTime.now());
        simChangeEventRepository.save(event);

        // Optional: invalidate all refresh tokens / sessions for this user here
        // e.g. tokenBlacklistService.blacklistAllForUser(userId);
    }

    private String maskFingerprint(String fp) {
        if (fp == null || fp.length() < 4) return "***";
        return fp.substring(0, Math.min(2, fp.length())) + "***";
    }
}

