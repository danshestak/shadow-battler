package com.shadowbattler.simulator.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shadowbattler.simulator.persistence.entity.SpeciesEntity;

@Repository
public interface SpeciesEntityRepository extends JpaRepository<SpeciesEntity, String> {
     @Query("SELECT DISTINCT s FROM SpeciesEntity s " +
           "LEFT JOIN FETCH s.fastMoveIds " +
           "LEFT JOIN FETCH s.chargedMoveIds " +
           "LEFT JOIN FETCH s.eliteMoveIds " +
           "LEFT JOIN FETCH s.legacyMoveIds")
    List<SpeciesEntity> findAllWithMoveIds();
}
