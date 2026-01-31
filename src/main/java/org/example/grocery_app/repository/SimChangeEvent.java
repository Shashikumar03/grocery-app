package org.example.grocery_app.repository;


import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity to persist SIM change events for security audit.
 */
@Entity
@Table(name = "sim_change_events")
public class SimChangeEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "previous_fingerprint", length = 255)
    private String previousFingerprint;

    @Column(name = "current_fingerprint", length = 255)
    private String currentFingerprint;

    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public LocalDateTime getDetectedAt() {
        return detectedAt;
    }

    public void setDetectedAt(LocalDateTime detectedAt) {
        this.detectedAt = detectedAt;
    }
}
