package com.shadowbattler.simulator.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shadowbattler.simulator.model.Species;

@Repository
public class JsonSpeciesRepository extends AbstractJsonRepository<Species> implements SpeciesRepository {
    public JsonSpeciesRepository(ObjectMapper objectMapper) {
        super(
            objectMapper, 
            "/static/species_data.json", 
            new TypeReference<List<Species>>() {}, 
            Species::speciesId 
        );
    }
}
