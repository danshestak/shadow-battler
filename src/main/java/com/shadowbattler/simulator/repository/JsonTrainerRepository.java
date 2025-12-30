package com.shadowbattler.simulator.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shadowbattler.simulator.model.Trainer;

@Repository
public class JsonTrainerRepository extends AbstractJsonRepository<Trainer> implements TrainerRepository {
    public JsonTrainerRepository(ObjectMapper objectMapper) {
        super(
            objectMapper, 
            "/static/trainer_data.json", 
            new TypeReference<List<Trainer>>() {}, 
            Trainer::getTrainerId 
        );
    }
}
