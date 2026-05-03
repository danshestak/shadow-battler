package com.shadowbattler.simulator.persistence.entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.shadowbattler.simulator.model.Lineup;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LineupStringConverter implements AttributeConverter<Lineup<String>, String> {
    private static final String SLOT_SEPARATOR = "|";
    private static final String LINEUP_SEPARATOR = "&";

    private static String toCharSeparatedString(Stream<String> stream, String separator) {
        return stream.collect(Collectors.joining(separator));
    }

    @Override
    public String convertToDatabaseColumn(Lineup<String> lineup) {
        if (lineup == null) return null;
        return toCharSeparatedString(
            lineup.stream().map(l -> LineupStringConverter.toCharSeparatedString(l.stream(), LineupStringConverter.SLOT_SEPARATOR)),
            LineupStringConverter.LINEUP_SEPARATOR
        );
    }

    @Override
    public Lineup<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) return Lineup.empty();
        final String[] slots = dbData.split(LineupStringConverter.LINEUP_SEPARATOR, -1);

        return new Lineup<>(
            slots.length > 0 ? slotFromString(slots[0]) : Collections.emptyList(),
            slots.length > 1 ? slotFromString(slots[1]) : Collections.emptyList(),
            slots.length > 2 ? slotFromString(slots[2]) : Collections.emptyList()
        );
    }

    private List<String> slotFromString(String slotString) {
        if (slotString == null || slotString.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(slotString.split(LineupStringConverter.SLOT_SEPARATOR, -1));
    }
}