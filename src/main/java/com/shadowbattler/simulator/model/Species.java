package com.shadowbattler.simulator.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Species(
    int dex,
    String speciesName,
    String speciesId,
    Stats3 baseStats,
    List<String> types,
    List<String> fastMoves,
    List<String> chargedMoves,
    List<String> tags,
    // Map<String, List<Double>> defaultIVs, //not necessary
    int level25CP,
    int buddyDistance,
    int thirdMoveCost,
    boolean released,
    Family family
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Family(
        String id,
        String parent,
        List<String> evolutions
    ) {}
}
