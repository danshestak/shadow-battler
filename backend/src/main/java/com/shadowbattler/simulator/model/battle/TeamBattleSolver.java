package com.shadowbattler.simulator.model.battle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shadowbattler.simulator.model.Creature;
import com.shadowbattler.simulator.model.Team;

public class TeamBattleSolver implements BattleSolver {
    private BattleResult battleResult = null;
    private FastBattleState fastBattleState = null;
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
    
    private void addStateWithPruning(List<FastBattleState> states, FastBattleState newState) {
        for (int i = 0; i < states.size(); i++) {
            if (newState.isDominatedBy(states.get(i))) {
                if (i > 0) {
                    final FastBattleState killer = states.get(i);
                    states.set(i, states.get(0));
                    states.set(0, killer);
                }
                return;
            }
        }

        for (int i = states.size() - 1; i >= 0; i--) {
            if (states.get(i).isDominatedBy(newState)) {
                final int last = states.size() - 1;
                states.set(i, states.get(last));
                states.remove(last);
            }
        }
        states.add(newState);
    }

    @Override
    public void solve() {
        List<FastBattleState> activeStates = new ArrayList<>();
        final List<FastBattleState> finishedStates = new ArrayList<>();
        int fastestWinTime = Integer.MAX_VALUE;

        activeStates.add(
            new FastBattleState(
                this.playerTeam,
                this.opponentTeam,
                (byte)this.opponentStartingShields,
                this.shouldLog
            )
        );

        while (!activeStates.isEmpty()) {
            //grouping states by how comparable they are to reduce the n in O(n^2) for pruning
            Map<Integer, List<FastBattleState>> groupedStates = new HashMap<>();
            for (FastBattleState state : activeStates) {
                List<? extends FastBattleState> newBranches = state.step();

                if (state.finished) {
                    finishedStates.add(state);
                    if (state.playerWon()) {
                        fastestWinTime = Math.min(fastestWinTime, state.timeElapsed);
                    }
                } else if (state.getProjTimeElapsedLowerBound() < fastestWinTime) {
                    groupedStates.computeIfAbsent(state.getComparisonKey(), k -> new ArrayList<>()).add(state);
                }

                for (FastBattleState branch : newBranches) {
                    if (branch.finished) {
                        finishedStates.add(branch);
                        if (branch.playerWon()) {
                            fastestWinTime = Math.min(fastestWinTime, branch.timeElapsed);
                        }
                    } else if (branch.getProjTimeElapsedLowerBound() < fastestWinTime) {
                        groupedStates.computeIfAbsent(branch.getComparisonKey(), k -> new ArrayList<>()).add(branch);
                    }
                }
            }

            List<FastBattleState> nextActiveStates = new ArrayList<>();
            for (List<FastBattleState> group : groupedStates.values()) {
                List<FastBattleState> prunedGroup = new ArrayList<>();
                for (FastBattleState state : group) {
                    addStateWithPruning(prunedGroup, state);
                }
                nextActiveStates.addAll(prunedGroup);
            }

            activeStates = nextActiveStates;
        }

        FastBattleState fastestWin = null;
        for (FastBattleState state : finishedStates) {
            if (!state.finished || !state.playerWon()) continue;

            if (fastestWin == null || state.timeElapsed < fastestWin.timeElapsed) {
                fastestWin = state;
            }
        }
        this.fastBattleState = fastestWin;

        if (fastestWin != null) {
            this.battleResult = new BattleResult(
                fastestWin.timeElapsed,
                1.0,
                0.5 //TODO: calculate hp percent
            );
        } else {
            this.battleResult = BattleResult.getLoss();
        }
    }

    @Override
    public BattleResult getBattleResult() {
        return this.battleResult;
    }

    public FastBattleState getFastBattleState() {
        return this.fastBattleState;
    }
}
