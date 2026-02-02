// package com.shadowbattler.simulator.persistence.entity;

// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.core.type.TypeReference;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.shadowbattler.simulator.model.Lineup;

// import jakarta.persistence.AttributeConverter;
// import jakarta.persistence.Converter;

// @Converter(autoApply = true)
// public class LineupStringConverter implements AttributeConverter<Lineup<String>, String> {
//     private final static ObjectMapper objectMapper = new ObjectMapper();

//     @Override
//     public String convertToDatabaseColumn(Lineup<String> lineup) {
//         if (lineup == null) return null;
//         try {
//             return objectMapper.writeValueAsString(lineup);
//         } catch (JsonProcessingException e) {
//             throw new IllegalArgumentException("Error converting Lineup to JSON", e);
//         }
//     }

//     @Override
//     public Lineup<String> convertToEntityAttribute(String dbData) {
//         if (dbData == null || dbData.isEmpty()) return Lineup.empty();
//         try {
//             TypeReference<Lineup<String>> typeRef = new TypeReference<>() {};
//             return objectMapper.readValue(dbData, typeRef);
//         } catch (JsonProcessingException e) {
//             throw new IllegalArgumentException("Error converting JSON to Lineup", e);
//         }
//     }
// }