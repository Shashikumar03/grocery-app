package org.example.grocery_app.payload;

//package com.shashi03.Bazzario.dto; // change to your package

/**
 * Request body when app reports SIM change (for security audit / token invalidation).
 */
public class SimChangeRequest {

    private String previousFingerprint;  // carrier|mcc|mnc before change
    private String currentFingerprint;   // carrier|mcc|mnc after change

    public SimChangeRequest() {}

    public SimChangeRequest(String previousFingerprint, String currentFingerprint) {
        this.previousFingerprint = previousFingerprint;
        this.currentFingerprint = currentFingerprint;
    }

    public String getPreviousFingerprint() {
        return previousFingerprint;
    }

    public void setPreviousFingerprint(String previousFingerprint) {
        this.previousFingerprint = previousFingerprint;
    }

    public String getCurrentFingerprint() {
        return currentFingerprint;
    }

    public void setCurrentFingerprint(String currentFingerprint) {
        this.currentFingerprint = currentFingerprint;
    }
}
