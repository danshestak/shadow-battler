package com.shadowbattler.simulator.service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shadowbattler.simulator.model.species.Species;

import jakarta.annotation.PostConstruct;

@Service
public class SpeciesDataService {
    private final Map<String, Species> speciesMap = new HashMap<>();

    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    public SpeciesDataService(ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void LoadSpeciesData() {
        try {
            Resource resource = resourceLoader.getResource("classpath:static/species_data.json");
            try (InputStream inputStream = resource.getInputStream()) {
                List<Species> speciesList = Arrays.asList(
                    objectMapper.readValue(inputStream, Species.class)
                );

                speciesList.forEach(s -> speciesMap.put(s.getSpeciesIdentity().getSpeciesId(), s));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
}
