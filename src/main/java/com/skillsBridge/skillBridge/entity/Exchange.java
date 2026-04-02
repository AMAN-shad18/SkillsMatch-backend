package com.skillsBridge.skillBridge.entity;

import com.skillsBridge.skillBridge.enums.ExchangeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Entity
    @Table(name = "exchanges")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Exchange {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "requester_id", nullable = false)
        private Users requester;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "provider_id", nullable = false)
        private Users provider;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "skill_offered_id", nullable = false)
        private Skill skillOffered;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "skill_requested_id", nullable = false)
        private Skill skillRequested;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private ExchangeStatus status = ExchangeStatus.PENDING;

        private LocalDateTime sessionDate;

        private String sessionLink;

        private Integer rating; // 1-5 rating given by requester

        private String feedback;

        @Column(nullable = false, updatable = false)
        private LocalDateTime createdAt;

        private LocalDateTime completedAt;

        // Relationships
        @OneToMany(mappedBy = "exchange", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Message> messages;

        @OneToOne(mappedBy = "exchange", cascade = CascadeType.ALL)
        private Review review;

        @PrePersist
        protected void onCreate() {
            createdAt = LocalDateTime.now();
        }
    }

