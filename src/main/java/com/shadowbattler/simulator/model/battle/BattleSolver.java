package com.shadowbattler.simulator.model.battle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.shadowbattler.simulator.model.Creature;
import com.shadowbattler.simulator.model.Team;

public class BattleSolver {
    private List<BattleState> activeStates;
    final private List<BattleState> finishedStates;

    public BattleSolver(Creature playerCreature, Team<Creature> opponentTeam, int opponentStartingShields) {
        this.activeStates = new ArrayList<>();
        this.finishedStates = new ArrayList<>();
        this.activeStates.add(new BattleState(playerCreature, opponentTeam, opponentStartingShields));
    }

    public BattleSolver(Team<Creature> playerTeam, Team<Creature> opponentTeam, int opponentStartingShields) {
        this.activeStates = new ArrayList<>();
        this.finishedStates = new ArrayList<>();
        this.activeStates.add(new BattleState(playerTeam, opponentTeam, opponentStartingShields));
    }

    public List<BattleState> getActiveStates() {
        return this.activeStates;
    }

    public List<BattleState> getFinishedStates() {
        return this.finishedStates;
    }
    
    /**
     * steps every active state, moving finished states into 
     * @return boolean representing if any states were stepped
     */
    public boolean stepAll() {
        if (activeStates.isEmpty()) {
            return false;
        }

        List<BattleState> potentialNextStates = new ArrayList<>();
        for (BattleState state : this.activeStates) {
            List<BattleState> newBranches = state.step();
            potentialNextStates.addAll(newBranches);

            if (state.isFinished()) {
                finishedStates.add(state);
            } else {
                potentialNextStates.add(state);
            }
        }

        //grouping states by how comparable they are to reduce the n in O(n^2) for pruning
        Map<List<Integer>, List<BattleState>> groupedStates = new HashMap<>();
        for (BattleState state : potentialNextStates) {
            if (state.isFinished()) {
                finishedStates.add(state);
                continue;
            }
            groupedStates.computeIfAbsent(state.getComparisonKey(), k -> new ArrayList<>()).add(state);
        }

        List<BattleState> nextActiveStates = new ArrayList<>();
        for (List<BattleState> group : groupedStates.values()) {
            List<BattleState> prunedGroup = new ArrayList<>();
            for (BattleState state : group) {
                addStateWithPruning(prunedGroup, state);
            }
            nextActiveStates.addAll(prunedGroup);
        }

        this.activeStates = nextActiveStates;
        return true;
    }

    private void addStateWithPruning(List<BattleState> states, BattleState newState) {
        for (BattleState existingState : states) {
            if (newState.isDominatedBy(existingState)) return;
        }

        states.removeIf((existingState) -> existingState.isDominatedBy(newState));
        states.add(newState);
    }

    public void finishAll() {
        while (stepAll()) {}
    }

    public Optional<BattleState> findFastestWin() {
        BattleState fastestWin = null;
        for (BattleState state : this.finishedStates) {
            if (!state.isFinished() || state.getPlayer().getActive().getRemainingHp() <= 0) continue;

            if (fastestWin == null || state.getTurnsElapsed() < fastestWin.getTurnsElapsed()) {
                fastestWin = state;
            }
        }
        return Optional.ofNullable(fastestWin);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BattleSolver{");
        sb.append("activeStates=").append(this.activeStates);
        sb.append("finishedStates=").append(this.finishedStates);
        sb.append('}');
        return sb.toString();
    }
}
