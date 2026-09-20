package com.shadowbattler.simulator.web.dto;

import com.shadowbattler.simulator.model.Stats3;

public record CreatureDTO(
    String species,
    User user,
    Double level,
    Stats3<Integer> ivs,
    String fast,
    String charged1,
    String charged2
) {
    public static enum User {
        PLAYER,
        OPPONENT;
    }
}
