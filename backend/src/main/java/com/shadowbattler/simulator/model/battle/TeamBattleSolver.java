package com.shadowbattler.simulator.model.battle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.shadowbattler.simulator.model.Creature;
import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.model.Team;

public class TeamBattleSolver implements BattleSolver {
    private BattleResult battleResult = null;
    private BattleState battleState = null;
    private final Team<Creature> playerTeam;
    private final Team<Creature> opponentTeam;
    private final int opponentStartingShields;
    private boolean shouldLog = false;

    public TeamBattleSolver(Team<Creature> playerTeam, Team<Creature> opponentTeam, int opponentStartingShields) {
        if (playerTeam.size() == 0 || opponentTeam.size() == 0) throw new IllegalStateException();

        this.playerTeam = playerTeam;
        this.opponentTeam = opponentTeam;
        this.opponentStartingShields = opponentStartingShields;

        this.battleState = new BattleState(
            this.playerTeam,
            this.opponentTeam,
            (byte)this.opponentStartingShields,
            this.shouldLog
        );
    }

    public TeamBattleSolver(Creature playerCreature, Team<Creature> opponentTeam, int opponentStartingShields) {
        if (playerCreature == null || opponentTeam.size() == 0) throw new IllegalStateException();

        this.playerTeam = new Team<>(playerCreature, null, null);
        this.opponentTeam = opponentTeam;
        this.opponentStartingShields = opponentStartingShields;
        
        this.battleState = new BattleState(
            this.playerTeam,
            this.opponentTeam,
            (byte)this.opponentStartingShields,
            this.shouldLog
        );
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

        activeStates.add(this.battleState);

        while (!activeStates.isEmpty()) {
            //grouping states by how comparable they are to reduce the n in O(n^2) for pruning
            Map<Integer, List<BattleState>> groupedStates = new HashMap<>();
            List<BattleState> newBranches = new ArrayList<>();
            for (BattleState state : activeStates) {
                state.step(newBranches);

                if (state.finished) {
                    finishedStates.add(state);
                    if (state.playerWon()) {
                        fastestWinTime = Math.min(fastestWinTime, state.timeElapsed);
                    }
                } else if (state.getProjTimeElapsedLowerBound() < fastestWinTime) {
                    groupedStates.computeIfAbsent(state.getComparisonKey(), k -> new ArrayList<>()).add(state);
                }

                for (BattleState branch : newBranches) {
                    if (branch.finished) {
                        finishedStates.add(branch);
                        if (branch.playerWon()) {
                            fastestWinTime = Math.min(fastestWinTime, branch.timeElapsed);
                        }
                    } else if (branch.getProjTimeElapsedLowerBound() < fastestWinTime) {
                        groupedStates.computeIfAbsent(branch.getComparisonKey(), k -> new ArrayList<>()).add(branch);
                    }
                }

                if (!newBranches.isEmpty()) newBranches.clear();
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
            if (!state.finished || !state.playerWon()) continue;

            if (fastestWin == null || state.timeElapsed < fastestWin.timeElapsed) {
                fastestWin = state;
            }
        }
        this.battleState = fastestWin;

        double hpPercent = 0;
        if (fastestWin != null) {
            int teamSize = 0;
            for (int i = 0; i < 3; i++) {
                final short maxHp = fastestWin.context.maxHp[i];
                if (maxHp <= 0) continue;
                hpPercent += (double)fastestWin.getHp(i)/maxHp;
                teamSize++;
            }
            hpPercent /= teamSize;
        }

        final Creature playerCreature = this.playerTeam.getFirst();
        final Move[] playerMoveset = playerCreature.getMoveset();
        final int trainerLevel = (int)this.opponentTeam.getFirst().getLevel();

        this.battleResult = new BattleResult(
            fastestWin == null ? -1 : fastestWin.timeElapsed,
            fastestWin == null ? 0.0 : 1.0,
            hpPercent,
            playerMoveset[0],
            playerMoveset[1],
            playerMoveset[2],
            playerCreature.getSpecies(),
            null,
            playerCreature.getLevel(),
            this.opponentTeam.stream().filter(Objects::nonNull).allMatch(c -> c.getLevel() == trainerLevel) ? trainerLevel : null
        );
    }

    @Override
    public BattleResult getBattleResult() {
        return this.battleResult;
    }

    public BattleState getBattleState() {
        return this.battleState;
    }
}
