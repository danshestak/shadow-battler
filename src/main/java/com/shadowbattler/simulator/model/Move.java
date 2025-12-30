package com.shadowbattler.simulator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.util.StdConverter;

@JsonDeserialize(converter = MoveJsonConverter.class)
public record Move(
    String moveId,
    String name,
    String abbreviation,
    Type type,
    int power,
    int energy,
    int energyGain,
    Stats3<Integer> buffsSelf,
    Stats3<Integer> buffsOpponent,
    double buffApplyChance,
    String archetype,
    int turns
) {}

class MoveJsonConverter extends StdConverter<MoveJsonConverter.MoveJson, Move> {
    @JsonIgnoreProperties(ignoreUnknown=true)
    static class MoveJson {
        public String moveId;
        public String name;
        public String abbreviation;
        public Type type;
        public int power;
        public int energy;
        public int energyGain;
        public int cooldown;
        public int[] buffs;
        public int[] buffsSelf;
        public int[] buffsOpponent;
        public String buffTarget;
        public double buffApplyChance;
        public String archetype;
        public int turns;
    }

    @Override
    public Move convert(MoveJson moveJson) {
        int[] buffsSelfArr = moveJson.buffsSelf;
        int[] buffsOpponentArr = moveJson.buffsOpponent;

        if (moveJson.buffTarget != null) {
            if (moveJson.buffTarget.equalsIgnoreCase("self") && buffsSelfArr == null) {
                buffsSelfArr = moveJson.buffs;
            } else if (moveJson.buffTarget.equalsIgnoreCase("opponent") && buffsOpponentArr == null) {
                buffsOpponentArr = moveJson.buffs;
            }
        }

        final Stats3<Integer> buffsSelfStats3;
        if (buffsSelfArr != null && buffsSelfArr.length >= 2) {
            buffsSelfStats3 = new Stats3<>(buffsSelfArr[0], buffsSelfArr[1], 0);
        } else {
            buffsSelfStats3 = null;
        }

        final Stats3<Integer> buffsOpponentStats3;
        if (buffsOpponentArr != null && buffsOpponentArr.length >= 2) {
            buffsOpponentStats3 = new Stats3<>(buffsOpponentArr[0], buffsOpponentArr[1], 0);
        } else {
            buffsOpponentStats3 = null;
        }

        return new Move(
            moveJson.moveId, 
            moveJson.name, 
            moveJson.abbreviation, 
            moveJson.type, 
            moveJson.power, 
            moveJson.energy, 
            moveJson.energyGain, 
            buffsSelfStats3, 
            buffsOpponentStats3, 
            moveJson.buffApplyChance, 
            moveJson.archetype,
            moveJson.turns
        );
    }
}
