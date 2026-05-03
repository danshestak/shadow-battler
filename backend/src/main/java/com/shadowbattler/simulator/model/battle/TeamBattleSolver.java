package com.shadowbattler.simulator.model.battle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.shadowbattler.simulator.model.Creature;
import com.shadowbattler.simulator.model.Team;

public class TeamBattleSolver implements BattleSolver {
    private BattleResult battleResult = null;
    private BattleState battleState = null;
    private final Team<Creature> playerTeam;
    private final Team<Creature> opponentTeam;
    private final int opponentStartingShields;
    private boolean shouldLog = false;

    public TeamBattleSolver(Team<Creature> playerTeam, Team<Creature> opponentTeam, int opponentStartingShields) {
        this.playerTeam = playerTeam;
        this.opponentTeam = opponentTeam;
        this.opponentStartingShields = opponentStartingShields;
    }

    public TeamBattleSolver(Creature playerCreature, Team<Creature> opponentTeam, int opponentStartingShields) {
        this.playerTeam = new Team<>(playerCreature, null, null);
        this.opponentTeam = opponentTeam;
        this.opponentStartingShields = opponentStartingShields;
    }

    public void enableLogging() {
        this.shouldLog = true;
    }
    
    private void addStateWithPruning(List<BattleState> states, BattleState newState) {
        for (BattleState existingState : states) {
            if (newState.isDominatedBy(existingState)) return;
        }

        states.removeIf((existingState) -> existingState.isDominatedBy(newState));
        states.add(newState);
    }

    @Override
    public void solve() {
        List<BattleState> activeStates = new ArrayList<>();
        final List<BattleState> finishedStates = new ArrayList<>();
        int fastestWinTime = Integer.MAX_VALUE;

        activeStates.add(
            new BattleState(
                this.playerTeam,
                this.opponentTeam,
                this.opponentStartingShields,
                this.shouldLog
            )
        );

        while (!activeStates.isEmpty()) {
            //grouping states by how comparable they are to reduce the n in O(n^2) for pruning
            Map<Long, List<BattleState>> groupedStates = new HashMap<>();
            for (BattleState state : activeStates) {
                List<BattleState> newBranches = state.step();

                if (state.isFinished()) {
                    finishedStates.add(state);
                    if (state.playerWon()) {
                        fastestWinTime = Math.min(fastestWinTime, state.getTimeElapsed());
                    }
                } else if (state.getTimeElapsed() < fastestWinTime) {
                    groupedStates.computeIfAbsent(state.getComparisonKey(), k -> new ArrayList<>()).add(state);
                }

                for (BattleState branch : newBranches) {
                    if (branch.isFinished()) {
                        finishedStates.add(branch);
                        if (branch.playerWon()) {
                            fastestWinTime = Math.min(fastestWinTime, branch.getTimeElapsed());
                        }
                    } else if (branch.getTimeElapsed() < fastestWinTime) {
                        groupedStates.computeIfAbsent(branch.getComparisonKey(), k -> new ArrayList<>()).add(branch);
                    }
                }
            }

            List<BattleState> nextActiveStates = new ArrayList<>();
            for (List<BattleState> group : groupedStates.values()) {
                List<BattleState> prunedGroup = new ArrayList<>();
                for (BattleState state : group) {
                    addStateWithPruning(prunedGroup, state);
                }
                nextActiveStates.addAll(prunedGroup);
            }

            activeStates = nextActiveStates;
        }

        BattleState fastestWin = null;
        for (BattleState state : finishedStates) {
            if (!state.isFinished() || !state.playerWon()) continue;

            if (fastestWin == null || state.getTimeElapsed() < fastestWin.getTimeElapsed()) {
                fastestWin = state;
            }
        }
        this.battleState = fastestWin;

        if (fastestWin != null) {
            this.battleResult = new BattleResult(
                fastestWin.getTimeElapsed(),
                fastestWin.playerWon() ? 1.0 : 0.0,
                fastestWin.getPlayer().getTeam().stream()
                    .filter(Objects::nonNull)
                    .mapToDouble((bc) -> bc.getRemainingHp()/bc.getCreature().getStats().getHp())
                    .average()
                    .orElse(0.0)
            );
        } else {
            this.battleResult = BattleResult.getLoss();
        }
    }

    @Override
    public BattleResult getBattleResult() {
        return this.battleResult;
    }

    public BattleState getBattleState() {
        return this.battleState;
    }
}
