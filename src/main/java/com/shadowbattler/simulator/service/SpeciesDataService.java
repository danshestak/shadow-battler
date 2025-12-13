package com.shadowbattler.simulator.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shadowbattler.simulator.model.species.Species;

import jakarta.annotation.PostConstruct;

@Service
public class SpeciesDataService {
    private Map<String, Species> speciesMap = new HashMap<>();

    private final ObjectMapper objectMapper;

    public SpeciesDataService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void loadSpeciesData() {
        try {
            ClassPathResource resource = new ClassPathResource("static/species_data.json");
            InputStream inputStream = resource.getInputStream();

            List<Species> speciesList = objectMapper.readValue(
                inputStream, 
                new TypeReference<List<Species>>() {}
            );

            this.speciesMap = speciesList.stream()
                .collect(Collectors.toMap(
                    Species::speciesId, 
                    Function.identity(), 
                    (existing, replacement) -> existing // in case of duplicate IDs, keep existing
                ));

            System.out.println("loaded " + speciesMap.size() + " species into memory.");

        } catch (IOException e) {
            throw new RuntimeException("failed to load JSON data", e);
        }
    }

    public Species getSpeciesById(String speciesId) {
        return this.speciesMap.get(speciesId);
    }
}
