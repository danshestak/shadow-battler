package com.shadowbattler.simulator.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shadowbattler.simulator.model.Opponent;

@Repository
public class JsonOpponentRepository extends AbstractJsonRepository<Opponent> implements OpponentRepository {
    public JsonOpponentRepository(ObjectMapper objectMapper) {
        super(
            objectMapper, 
            "/static/opponent_data.json", 
            new TypeReference<List<Opponent>>() {}, 
            Opponent::getOpponentId
        );
    }
}
