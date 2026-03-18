package com.shadowbattler.simulator.persistence.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.shadowbattler.simulator.persistence.entity.SpeciesEntity;
import com.shadowbattler.simulator.persistence.repository.SpeciesEntityRepository;

@Service
public class SpeciesEntityService {
    private final SpeciesEntityRepository speciesEntityRepository;

    public SpeciesEntityService(SpeciesEntityRepository speciesEntityRepository) {
        this.speciesEntityRepository = speciesEntityRepository;
    }

    public Optional<SpeciesEntity> getSpeciesEntityById(String id) {
        return this.speciesEntityRepository.findById(id);
    }
}
