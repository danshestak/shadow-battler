package com.shadowbattler.simulator.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public boolean speciesExists(String id) {
        try {
            this.getSpeciesById(id);
        } catch (ResourceNotFoundException e) {
            return false;
        }
        return true;
    }

    public void createShadowFor(String speciesId) {
        final Species nonShadow = this.speciesRepository.findById(speciesId).orElse(null);
        if (nonShadow == null) {
            throw new IllegalArgumentException(
                String.format("there is no species of speciesId %s", speciesId)
            );
        }

        if (nonShadow.getTags().contains(Species.Tag.SHADOW)) {
            throw new IllegalArgumentException(
                String.format("expected nonshadow speciesId, received %s", speciesId)
            );
        }

        final String shadowSpeciesId = speciesId + "_shadow";

        if (this.speciesRepository.findById(shadowSpeciesId).isPresent()) {
            throw new IllegalArgumentException(
                String.format("speciesId %s is already a shadow for speciesId %s", shadowSpeciesId, speciesId)
            );
        }

        final List<Species.Tag> shadowTags = new ArrayList<>(nonShadow.getTags());
        shadowTags.add(Species.Tag.SHADOW);

        final Species.Family shadowFamily = nonShadow.getFamily() == null ? null : new Species.Family(
            nonShadow.getFamily().id(), 
            null, 
            null
        );

        final Species shadow = new Species(
            nonShadow.getDex(), 
            nonShadow.getSpeciesName() + " (Shadow)", 
            shadowSpeciesId, 
            nonShadow.getBaseStats(), 
            Arrays.copyOf(nonShadow.getTypes(), nonShadow.getTypes().length), 
            new ArrayList<>(nonShadow.getFastMoveIds()),
            new ArrayList<>(nonShadow.getChargedMoveIds()),
            new ArrayList<>(nonShadow.getEliteMoveIds()),
            new ArrayList<>(nonShadow.getLegacyMoveIds()),
            shadowTags,
            nonShadow.getBuddyDistance(), 
            nonShadow.getThirdMoveCost(), 
            false, 
            shadowFamily
        );
        shadow.hydrate(this.movesDataService);

        this.speciesRepository.save(shadow);
    }

    @PostConstruct
    public void hydrateAll() {
        this.speciesRepository.findAll().forEach((Species s) -> {
            s.hydrate(this.movesDataService);
        });
    }
}
