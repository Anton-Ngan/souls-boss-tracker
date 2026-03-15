package com.soulstracker.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "boss_attempt")
@Getter @Setter @NoArgsConstructor
public class BossAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boss_id", nullable = false)
    private Boss boss;

    @Column(nullable = false)
    private LocalDateTime attemptedAt = LocalDateTime.now();

    @Column(nullable = false)
    private boolean died = true;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
