package com.shadowbattler.simulator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Move(
    String moveId,
    String name,
    String abbreviation,
    String type,
    int power,
    int energy,
    int energyGain,
    int cooldown,
    int[] buffs,
    String buffTarget,
    String buffApplyChance,
    String archetype,
    int turns
) {}
