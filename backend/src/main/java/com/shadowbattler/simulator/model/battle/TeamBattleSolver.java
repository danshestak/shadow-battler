package com.shadowbattler.simulator.model.battle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

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
    
    private void addStateWithPruning(
        Map<Integer, List<BattleState>> pendingStatesByKey,
        PriorityQueue<BattleState> activeStates,
        Set<BattleState> queuedStates,
        BattleState newState,
        int fastestWinTime
    ) {
        if (newState.getProjTimeElapsedLowerBound() > fastestWinTime) return;

        final int comparisonKey = newState.getComparisonKey();
        final List<BattleState> states = pendingStatesByKey.computeIfAbsent(comparisonKey, key -> new ArrayList<>());

        for (BattleState state : states) {
            if (newState.isDominatedBy(state)) return;
        }

        for (int i = states.size() - 1; i >= 0; i--) {
            final BattleState state = states.get(i);
            if (state.isDominatedBy(newState)) {
                states.remove(i);
                queuedStates.remove(state);
            }
        }

        states.add(newState);
        activeStates.add(newState);
        queuedStates.add(newState);
    }

    private static void removePendingState(
        Map<Integer, List<BattleState>> pendingStatesByKey,
        int comparisonKey,
        BattleState state
    ) {
        final List<BattleState> states = pendingStatesByKey.get(comparisonKey);
        if (states == null) return;

        states.remove(state);
        if (states.isEmpty()) pendingStatesByKey.remove(comparisonKey);
    }

    @Override
    public void solve() {
        final PriorityQueue<BattleState> activeStates = new PriorityQueue<>(
            Comparator.comparingInt((BattleState b) -> b.getProjTimeElapsedLowerBound())
                .thenComparing((first, second) -> Integer.compare(second.timeElapsed, first.timeElapsed)
            )
        );
        final Set<BattleState> queuedStates = Collections.newSetFromMap(new IdentityHashMap<>());
        final Map<Integer, List<BattleState>> pendingStatesByKey = new HashMap<>();
        final List<BattleState> newBranches = new ArrayList<>();
        int fastestWinTime = Integer.MAX_VALUE;
        BattleState fastestWin = null;

        addStateWithPruning(
            pendingStatesByKey,
            activeStates,
            queuedStates,
            this.battleState,
            fastestWinTime
        );

        while (!activeStates.isEmpty()) {
            final BattleState state = activeStates.remove();
            if (!queuedStates.remove(state)) continue;

            if (state.getProjTimeElapsedLowerBound() > fastestWinTime) break;
            removePendingState(pendingStatesByKey, state.getComparisonKey(), state);

            state.step(newBranches);
            if (state.finished) {
                if (state.playerWon() && isBetterWin(state, fastestWin)) {
                    fastestWin = state;
                    fastestWinTime = state.timeElapsed;
                }
            } else {
                addStateWithPruning(
                    pendingStatesByKey,
                    activeStates,
                    queuedStates,
                    state,
                    fastestWinTime
                );
            }

            for (BattleState branch : newBranches) {
                if (branch.finished) {
                    if (branch.playerWon() && isBetterWin(branch, fastestWin)) {
                        fastestWin = branch;
                        fastestWinTime = branch.timeElapsed;
                    }
                } else {
                    addStateWithPruning(
                        pendingStatesByKey,
                        activeStates,
                        queuedStates,
                        branch,
                        fastestWinTime
                    );
                }
            }
            newBranches.clear();
        }

        this.battleState = fastestWin;

        final double hpPercent = fastestWin == null ? 0 : getPlayerHpPercent(fastestWin);
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

    private static boolean isBetterWin(BattleState candidate, BattleState currentBest) {
        return currentBest == null
            || candidate.timeElapsed < currentBest.timeElapsed
            || (candidate.timeElapsed == currentBest.timeElapsed
                && getPlayerHpPercent(candidate) > getPlayerHpPercent(currentBest));
    }

    private static double getPlayerHpPercent(BattleState state) {
        double hpPercent = 0;
        int teamSize = 0;
        for (int i = 0; i < 3; i++) {
            final short maxHp = state.context.maxHp[i];
            if (maxHp <= 0) continue;
            hpPercent += (double)state.getHp(i) / maxHp;
            teamSize++;
        }
        return teamSize == 0 ? 0 : hpPercent / teamSize;
    }

    @Override
    public BattleResult getBattleResult() {
        return this.battleResult;
    }

    public BattleState getBattleState() {
        return this.battleState;
    }
}
