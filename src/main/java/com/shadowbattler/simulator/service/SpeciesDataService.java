package com.shadowbattler.simulator.service;

import org.springframework.stereotype.Service;

import com.shadowbattler.exception.ResourceNotFoundException;
import com.shadowbattler.simulator.model.Species;
import com.shadowbattler.simulator.repository.SpeciesRepository;

import jakarta.annotation.PostConstruct;

@Service
public class SpeciesDataService {
    private final SpeciesRepository speciesRepository;
    private final MovesDataService movesDataService;

    public SpeciesDataService(SpeciesRepository speciesRepository, MovesDataService movesDataService) {
        this.speciesRepository = speciesRepository;
        this.movesDataService = movesDataService;
    }

    public Species getSpeciesById(String id) {
        return this.speciesRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(String.format("species of id %s not found", id)));
    }

    @PostConstruct
    public void hydrateAll() {
        this.speciesRepository.findAll().forEach((Species s) -> {
            s.hydrate(this.movesDataService);
        });
    }
}
