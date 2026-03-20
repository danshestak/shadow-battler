package com.shadowbattler.simulator.model.battle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.shadowbattler.simulator.model.Creature;
import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.model.Species;

public class MovesetSolver implements BattleSolver {
    private List<MovesetBattleResult> movesetBattleResults;
    private final Species species;
    private final BiFunction<Move, List<Move>, Creature> creatureFactory;
    private final Function<Creature, BattleSolver> battleSolverFactory;
    
    public static class MovesetBattleResult implements Comparable<MovesetBattleResult> {
        Move[] moveset;
        final BattleResult battleResult;

        public MovesetBattleResult(Move[] moveset, BattleResult battleResult) {
            this.moveset = moveset;
            this.battleResult = battleResult;
        }

        @Override
        public int compareTo(MovesetBattleResult o) {
            if (this.battleResult == null && o.battleResult == null) return 0;
            if (this.battleResult == null) return -1;
            if (o.battleResult == null) return 1;
            return Integer.valueOf(this.battleResult.getScore()).compareTo(o.battleResult.getScore());
        }

        @Override
        public String toString() {
            return "MovesetBattleResult [moveset=" + Arrays.toString(moveset) + ", battleResult=" + battleResult + "]";
        }

        public Move[] getMoveset() {
            return this.moveset;
        }

        public void setMoveset(Move[] moveset) {
            this.moveset = moveset;
        }

        public BattleResult getBattleResult() {
            return this.battleResult;
        }

        
    }

    public MovesetSolver(BiFunction<Move, List<Move>, Creature> creatureFactory, Function<Creature, BattleSolver> battleSolverFactory) {
        this.species = creatureFactory.apply(null, null).getSpecies();
        this.creatureFactory = creatureFactory;
        this.battleSolverFactory = battleSolverFactory;
    }

    @Override
    public void solve() {
        if (this.movesetBattleResults != null) return;

        final int moveCombinations = this.species.moveCombinationQuantity(false);

        final List<Move[]> movesets = new ArrayList<>(moveCombinations);
        for (int i = 0; i < moveCombinations; i++) {
            movesets.add(this.species.moveCombinationFromId(i, false));
        }

        this.movesetBattleResults = movesets.stream()
            .map((moveset) -> {
                Creature playerCreature = this.creatureFactory.apply(moveset[0], getChargedMoves(moveset));
                BattleSolver battleSolver = battleSolverFactory.apply(playerCreature);
                battleSolver.solve();
                return new MovesetBattleResult(moveset, battleSolver.getBattleResult());
            })
            .collect(Collectors.toCollection(ArrayList::new));
        
        this.movesetBattleResults.sort(Comparator.reverseOrder());
    }

    private static List<Move> getChargedMoves(Move[] moveset) {
        final Move charged1 = moveset[1];
        final Move charged2 = moveset[2];

        if (charged1 != null) {
            if (charged2 != null) {
                return List.of(charged1, charged2);
            } else {
                return List.of(charged1);
            }
        } else {
            return List.of();
        }
    }

    @Override
    public BattleResult getBattleResult() {
        if (this.movesetBattleResults.isEmpty()) return null;
        return this.movesetBattleResults.get(0).battleResult;
    }

    public Move[] getMoveset() {
        if (this.movesetBattleResults.isEmpty()) return null;
        return this.movesetBattleResults.get(0).moveset;
    }

    public List<MovesetBattleResult> getMovesetBattleResults() {
        return this.movesetBattleResults;
    }
}
