package com.shadowbattler.simulator.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shadowbattler.simulator.persistence.entity.OpponentEntity;

@Repository
public interface OpponentEntityRepository extends JpaRepository<OpponentEntity, String> {
    @Query("SELECT DISTINCT o FROM OpponentEntity o LEFT JOIN FETCH o.lineupSpeciesIds")
    List<OpponentEntity> findAllWithLineups();
}
