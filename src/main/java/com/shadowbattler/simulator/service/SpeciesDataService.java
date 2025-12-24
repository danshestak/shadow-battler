package com.shadowbattler.simulator.service;

import org.springframework.stereotype.Service;

import com.shadowbattler.exception.ResourceNotFoundException;
import com.shadowbattler.simulator.model.Species;
import com.shadowbattler.simulator.repository.SpeciesRepository;

@Service
public class SpeciesDataService {
    SpeciesRepository speciesRepository;

    public SpeciesDataService(SpeciesRepository speciesRepository) {
        this.speciesRepository = speciesRepository;
    }

    public Species getSpeciesById(String id) {
        return this.speciesRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(String.format("species of id %s not found", id)));
    }
}
