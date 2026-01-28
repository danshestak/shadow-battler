package com.shadowbattler.simulator.model.battle;

public interface BattleSolver {
    /**
     * runs the solving algorithm
     */
    public void solve();

    /**
     * @return the BattleResult the BattleSolver is used to solve for
     */
    public BattleResult getBattleResult();
}
