package com.shadowbattler.simulator.model.battle;

import java.util.List;

public interface BattleState {
    boolean isDominatedBy(BattleState other);

    List<? extends BattleState> step();

    boolean isFinished();

    boolean playerWon();

    int getTimeElapsed();

    int getProjTimeElapsedLowerBound();

    int getComparisonKey();

    Trainer getPlayer();

    Trainer getEnemy();

    BattleLog getLog();
}