package com.shadowbattler.simulator.persistence.entity;

import com.shadowbattler.simulator.model.Stats3;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class Stats3IntegerConverter implements AttributeConverter<Stats3<Integer>, String> {
    @Override
    public String convertToDatabaseColumn(Stats3<Integer> stats) {
        if (stats == null) return null;
        return String.valueOf(stats.getAtk()) + "," 
            + String.valueOf(stats.getDef()) + "," 
            + String.valueOf(stats.getHp());
    }

    @Override
    public Stats3<Integer> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return null;
        final String[] split = dbData.split(",");
        return new Stats3<>(
            Integer.valueOf(split[0]),
            Integer.valueOf(split[1]),
            Integer.valueOf(split[2])
        );
    }
}