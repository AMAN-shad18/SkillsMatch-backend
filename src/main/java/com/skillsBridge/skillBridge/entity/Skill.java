package com.skillsBridge.skillBridge.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;




    @Entity
    @Table(name = "skills")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Skill {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(unique = true, nullable = false)
        private String name;

        private String category;

        private String description;

        private Integer popularityScore = 0;

        // Relationship
        @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<UserSkill> userSkills = new ArrayList<>();

        @OneToMany(mappedBy = "skillOffered", cascade = CascadeType.ALL)
        private List<Exchange> offeredInExchanges = new ArrayList<>();

        @OneToMany(mappedBy = "skillRequested", cascade = CascadeType.ALL)
        private List<Exchange> requestedInExchanges = new ArrayList<>();
    }

