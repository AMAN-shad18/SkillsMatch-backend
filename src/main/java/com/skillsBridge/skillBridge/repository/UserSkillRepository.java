package com.skillsBridge.skillBridge.repository;

import com.skillsBridge.skillBridge.entity.UserSkill;
import com.skillsBridge.skillBridge.entity.Users;
import com.skillsBridge.skillBridge.enums.SkillType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {
    List<UserSkill> findByUser(Users user);
    List<UserSkill> findByUserAndSkillType(Users user, SkillType skillType);
    void deleteByUser(Users user);
    Optional<UserSkill> findByUserAndSkillId(Users user, Long skillId);
}
