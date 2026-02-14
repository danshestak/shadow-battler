package com.shadowbattler.simulator.persistence.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shadowbattler.simulator.model.Stats3;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class Stats3IntegerConverter implements AttributeConverter<Stats3<Integer>, String> {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Stats3<Integer> stats) {
        if (stats == null) return null;
        try {
            return objectMapper.writeValueAsString(stats);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting Stats3<Integer> to JSON", e);
        }
    }

    @Override
    public Stats3<Integer> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return null;
        try {
            return objectMapper.readValue(dbData, new TypeReference<Stats3<Integer>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to Stats3<Integer>", e);
        }
    }
}