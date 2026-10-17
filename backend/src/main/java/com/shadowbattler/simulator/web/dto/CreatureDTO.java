package com.shadowbattler.simulator.web.dto;

import com.shadowbattler.simulator.model.Stats3;

public record CreatureDTO(
    String species,
    Double level,
    Stats3<Integer> ivs,
    String fast,
    String charged1,
    String charged2
) {}
