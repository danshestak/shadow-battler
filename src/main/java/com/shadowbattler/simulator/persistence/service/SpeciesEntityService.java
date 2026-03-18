package com.shadowbattler.simulator.persistence.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.shadowbattler.simulator.model.Species;
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

    public SpeciesEntity saveSpecies(Species species) {
        SpeciesEntity entity = this.speciesEntityRepository.findById(species.getSpeciesId()).orElse(new SpeciesEntity());
        
        entity.updateFromSpecies(species);
        //by saving the entity with an empty list, orphanRemoval will delete the old results.
        entity.setBattleResults(new ArrayList<>());
        return this.speciesEntityRepository.save(entity);
    }
}
