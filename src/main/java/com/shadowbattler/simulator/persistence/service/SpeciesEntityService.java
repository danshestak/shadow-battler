package com.shadowbattler.simulator.persistence.service;

import java.util.List;
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

    public List<SpeciesEntity> getAllSpeciesEntities() {
        return this.speciesEntityRepository.findAll();
    }

    public List<SpeciesEntity> getAllSpeciesEntitiesWithMoveIds() {
        return this.speciesEntityRepository.findAllWithMoveIds();
    }

    public Optional<SpeciesEntity> getSpeciesEntityById(String id) {
        return this.speciesEntityRepository.findById(id);
    }

    public SpeciesEntity getReferenceById(String id) {
        return this.speciesEntityRepository.getReferenceById(id);
    }

    public SpeciesEntity saveSpecies(Species species) {
        SpeciesEntity entity = this.speciesEntityRepository.findById(species.getSpeciesId()).orElse(new SpeciesEntity());
        
        entity.updateFromSpecies(species);
        entity.getBattleResults().clear(); //triggers orphan removal
        return this.speciesEntityRepository.save(entity);
    }
}
