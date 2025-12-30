package com.shadowbattler.simulator.service;

import org.springframework.stereotype.Service;

import com.shadowbattler.exception.ResourceNotFoundException;
import com.shadowbattler.simulator.model.Trainer;
import com.shadowbattler.simulator.repository.TrainerRepository;

import jakarta.annotation.PostConstruct;

@Service
public class TrainerDataService {
    private final TrainerRepository trainerRepository;
    private final SpeciesDataService speciesDataService;

    public TrainerDataService(TrainerRepository trainerRepository, SpeciesDataService speciesDataService) {
        this.trainerRepository = trainerRepository;
        this.speciesDataService = speciesDataService;
    }

    public Trainer getTrainerById(String id) {
        return this.trainerRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(String.format("trainer of id %s not found", id)));
    }
    
    @PostConstruct
    public void hydrateAll() {
        this.trainerRepository.findAll().forEach((Trainer t) -> {
            t.hydrate(speciesDataService);
        });
    }
}
