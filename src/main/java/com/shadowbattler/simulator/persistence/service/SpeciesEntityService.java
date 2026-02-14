package com.shadowbattler.simulator.persistence.service;

import org.springframework.stereotype.Service;

import com.shadowbattler.simulator.persistence.repository.SpeciesEntityRepository;

@Service
public class SpeciesEntityService {
    private final SpeciesEntityRepository speciesEntityRepository;

    public SpeciesEntityService(SpeciesEntityRepository speciesEntityRepository) {
        this.speciesEntityRepository = speciesEntityRepository;
    }

    
}
