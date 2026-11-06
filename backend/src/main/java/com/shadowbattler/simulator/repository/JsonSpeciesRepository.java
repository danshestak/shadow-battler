package com.shadowbattler.simulator.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shadowbattler.simulator.model.Species;

@Repository
public class JsonSpeciesRepository extends AbstractJsonRepository<Species> implements SpeciesRepository {
    private int hash = 0;
    private boolean isHashStale = true;

    public JsonSpeciesRepository(ObjectMapper objectMapper) {
        super(
            objectMapper, 
            "species_data.json", 
            new TypeReference<List<Species>>() {}, 
            s -> s.getSpeciesId()
        );
    }

    @Override
    public void save(Species species) {
        this.cache.put(species.getSpeciesId(), species);
        this.isHashStale = true;
    }
    
    @Override 
    public int hashCode() {
        if (this.isHashStale) {
            final int newHash = this.cache.hashCode();
            this.hash = newHash;
            this.isHashStale = false;
        }
        return this.hash;
    }
}
