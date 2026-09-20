package com.shadowbattler.simulator.web.dto;

public record BattleRequest(
    String opponentId,
    int trainerLevel,
    boolean solveForMoveset,
    EnemyMode enemyMode,
    CreatureDTO playerCreature,
    CreatureDTO enemyCreature1,
    CreatureDTO enemyCreature2,
    CreatureDTO enemyCreature3
) {
    public static enum EnemyMode {
        TEAM,
        LINEUP;
    }
}
