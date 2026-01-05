package com.shadowbattler.simulator.repository;

import com.shadowbattler.simulator.model.Species;

public interface SpeciesRepository extends Repository<Species, String> {
    public void save(Species species);
}