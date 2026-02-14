package com.shadowbattler.simulator.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shadowbattler.simulator.persistence.entity.SpeciesEntity;

@Repository
public interface SpeciesEntityRepository extends JpaRepository<SpeciesEntity, String> {

}
