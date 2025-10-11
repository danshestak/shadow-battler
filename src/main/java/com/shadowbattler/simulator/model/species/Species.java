package com.shadowbattler.simulator.model.species;

import java.util.Arrays;

public class Species {
    private final SpeciesIdentity speciesIdentity;
    private final Stats3 baseStats;
    private final String[] types;
    private final Learnset learnset;
    private final SpeciesFamily speciesFamily;
    private final SpeciesCosts speciesCosts;
    private final SpeciesMetadata speciesMetadata;
    
    public Species(
        SpeciesIdentity speciesIdentity, 
        Stats3 baseStats, 
        String[] types, 
        Learnset learnset, 
        SpeciesFamily speciesFamily, 
        SpeciesCosts speciesCosts,
        SpeciesMetadata speciesMetadata) {
        this.baseStats = baseStats;
        this.learnset = learnset;
        this.speciesFamily = speciesFamily;
        this.speciesIdentity = speciesIdentity;
        this.speciesMetadata = speciesMetadata;
        this.speciesCosts = speciesCosts;
        this.types = Arrays.copyOf(types, types.length);
    }

    public SpeciesIdentity getSpeciesIdentity() {
        return speciesIdentity;
    }

    public Stats3 getBaseStats() {
        return baseStats;
    }

    public String[] getTypes() {
        return Arrays.copyOf(types, types.length);
    }

    public Learnset getLearnset() {
        return learnset;
    }

    public SpeciesFamily getSpeciesFamily() {
        return speciesFamily;
    }

    public SpeciesCosts getSpeciesCosts() {
        return speciesCosts;
    }

    public SpeciesMetadata getSpeciesMetadata() {
        return speciesMetadata;
    }
}