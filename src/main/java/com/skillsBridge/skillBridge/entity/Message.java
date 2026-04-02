package com.skillsBridge.skillBridge.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Id;

import java.time.LocalDateTime;


    @Entity
    @Table(name = "messages")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Message {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "sender_id", nullable = false)
        private Users sender;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "receiver_id", nullable = false)
        private Users receiver;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "exchange_id")
        private Exchange exchange;

        @Column(nullable = false, length = 1000)
        private String content;

        private Boolean isRead = false;

        @Column(nullable = false, updatable = false)
        private LocalDateTime timestamp;

        @PrePersist
        protected void onCreate() {
            timestamp = LocalDateTime.now();
        }

    }
