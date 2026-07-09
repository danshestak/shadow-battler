package com.shadowbattler.simulator.model.battle;

import java.util.ArrayList;
import java.util.Arrays;
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
        for (int i = 0; i < states.size(); i++) {
            if (newState.isDominatedBy(states.get(i))) {
                if (i > 0) {
                    final BattleState killer = states.get(i);
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
            Map<Integer, List<BattleState>> groupedStates = new HashMap<>();
            for (BattleState state : activeStates) {
                List<BattleState> newBranches = state.step();

                if (state.isFinished()) {
                    finishedStates.add(state);
                    if (state.playerWon()) {
                        fastestWinTime = Math.min(fastestWinTime, state.getTimeElapsed());
                    }
                } else if (state.getProjTimeElapsedLowerBound() < fastestWinTime) {
                    groupedStates.computeIfAbsent(state.getComparisonKey(), k -> new ArrayList<>()).add(state);
                }

                for (BattleState branch : newBranches) {
                    if (branch.isFinished()) {
                        finishedStates.add(branch);
                        if (branch.playerWon()) {
                            fastestWinTime = Math.min(fastestWinTime, branch.getTimeElapsed());
                        }
                    } else if (branch.getProjTimeElapsedLowerBound() < fastestWinTime) {
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
                1.0,
                Arrays.stream(fastestWin.getPlayer().getBattlingCreatures())
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
