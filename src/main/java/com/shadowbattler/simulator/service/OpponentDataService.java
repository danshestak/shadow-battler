package com.shadowbattler.simulator.service;

import org.springframework.stereotype.Service;

import com.shadowbattler.exception.ResourceNotFoundException;
import com.shadowbattler.simulator.model.Opponent;
import com.shadowbattler.simulator.repository.OpponentRepository;

import jakarta.annotation.PostConstruct;

@Service
public class OpponentDataService {
    private final OpponentRepository opponentRepository;
    private final SpeciesDataService speciesDataService;

    public OpponentDataService(OpponentRepository opponentRepository, SpeciesDataService speciesDataService) {
        this.opponentRepository = opponentRepository;
        this.speciesDataService = speciesDataService;
    }

    public Opponent getOpponentById(String id) {
        return this.opponentRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(String.format("opponent of id %s not found", id)));
    }
    
    @PostConstruct
    public void hydrateAll() {
        this.opponentRepository.findAll().forEach((Opponent o) -> {
            o.hydrate(speciesDataService);
        });
    }
}
