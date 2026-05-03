package com.shadowbattler.simulator.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shadowbattler.simulator.persistence.entity.BattleResultEntity;

@Repository
public interface BattleResultEntityRepository extends JpaRepository<BattleResultEntity, Integer>{
    @Query("SELECT b.playerSpecies.speciesId, b.playerFastMove.moveId, b.playerChargedMove1.moveId, b.playerChargedMove2.moveId " +
           "FROM BattleResultEntity b " +
           "WHERE b.playerSpecies IS NOT NULL " +
           "GROUP BY b.playerSpecies.speciesId, b.playerFastMove.moveId, b.playerChargedMove1.moveId, b.playerChargedMove2.moveId")
    List<Object[]> findDistinctMovesetsPerSpecies();
}
