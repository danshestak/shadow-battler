package com.shadowbattler.simulator.model.species;

import java.util.Arrays;

public class SpeciesFamily {
    private final String familyId;
    private final String parent;
    private final String[] evolutions;

    public SpeciesFamily(String[] evolutions, String familyId, String parent) {
        this.evolutions = Arrays.copyOf(evolutions, evolutions.length);
        this.familyId = familyId;
        this.parent = parent;
    }

    public String getFamilyId() {
        return familyId;
    }

    public String getParent() {
        return parent;
    }

    public String[] getEvolutions() {
        return Arrays.copyOf(evolutions, evolutions.length);
    }
}
