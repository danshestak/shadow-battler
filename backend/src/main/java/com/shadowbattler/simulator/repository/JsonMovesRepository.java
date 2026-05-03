package com.shadowbattler.simulator.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shadowbattler.simulator.model.Move;

@Repository
public class JsonMovesRepository extends AbstractJsonRepository<Move> implements MovesRepository {
    public JsonMovesRepository(ObjectMapper objectMapper) {
        super(
            objectMapper, 
            "/static/moves_data.json", 
            new TypeReference<List<Move>>() {}, 
            Move::moveId 
        );
    }
}
