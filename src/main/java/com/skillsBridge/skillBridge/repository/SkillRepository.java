package com.skillsBridge.skillBridge.repository;

import com.skillsBridge.skillBridge.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    Optional<Skill> findByName(String name);
    List<Skill> findByCategory(String category);
    List<Skill> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT s FROM Skill s ORDER BY s.popularityScore DESC LIMIT 10")
    List<Skill> findTopPopularSkills();
    
    @Query("SELECT DISTINCT s.category FROM Skill s")
    List<String> findAllCategories();
}
