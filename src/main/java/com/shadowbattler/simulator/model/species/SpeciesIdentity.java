package com.shadowbattler.simulator.model.species;

public class SpeciesIdentity {
    private final int dex;
    private final String speciesName;
    private final String speciesId;

    public SpeciesIdentity(int dex, String speciesId, String speciesName) {
        this.dex = dex;
        this.speciesId = speciesId;
        this.speciesName = speciesName;
    }

    public int getDex() {
        return dex;
    }

    public String getSpeciesName() {
        return speciesName;
    }

    public String getSpeciesId() {
        return speciesId;
    }
}
