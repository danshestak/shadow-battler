package com.shadowbattler.simulator.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shadowbattler.simulator.persistence.entity.BattleResultEntity;

@Repository
public interface BattleResultEntityRepository extends JpaRepository<BattleResultEntity, Integer>{
    @Query("SELECT b.playerSpecies.speciesId, b.playerFastMove.moveId, b.playerChargedMove1.moveId, b.playerChargedMove2.moveId " +
           "FROM BattleResultEntity b " +
           "WHERE b.playerSpecies IS NOT NULL " +
           "GROUP BY b.playerSpecies.speciesId, b.playerFastMove.moveId, b.playerChargedMove1.moveId, b.playerChargedMove2.moveId")
    List<Object[]> findDistinctMovesetsPerSpecies();

    @Query(
        value = """
            SELECT * FROM (
                SELECT br.*, ROW_NUMBER() OVER (PARTITION BY br.player_species_id ORDER BY br.score DESC) as rn
                FROM battle_results br
                WHERE br.opponent_id = :opponentId
            ) b
            WHERE b.rn = 1
            ORDER BY b.score DESC
            """,
        nativeQuery = true)
    List<BattleResultEntity> findTopScoringBRsPerSpecies(@Param("opponentId") String opponentId);
}
