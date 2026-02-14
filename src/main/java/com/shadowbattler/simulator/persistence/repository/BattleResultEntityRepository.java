package com.shadowbattler.simulator.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shadowbattler.simulator.persistence.entity.BattleResultEntity;

@Repository
public interface BattleResultEntityRepository extends JpaRepository<BattleResultEntity, Integer>{

}
