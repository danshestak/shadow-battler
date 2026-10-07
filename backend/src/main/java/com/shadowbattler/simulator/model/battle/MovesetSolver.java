package com.shadowbattler.simulator.model.battle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.shadowbattler.simulator.model.Creature;
import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.model.Species;

public class MovesetSolver implements BattleSolver {
    private List<BattleResult> battleResults;
    private final Species species;
    private final BiFunction<Move, List<Move>, Creature> creatureFactory;
    private final Function<Creature, BattleSolver> battleSolverFactory;

    public MovesetSolver(BiFunction<Move, List<Move>, Creature> creatureFactory, Function<Creature, BattleSolver> battleSolverFactory) {
        this.species = creatureFactory.apply(null, null).getSpecies();
        this.creatureFactory = creatureFactory;
        this.battleSolverFactory = battleSolverFactory;
    }

    @Override
    public void solve() {
        if (this.battleResults != null) return;

        final int moveCombinations = this.species.moveCombinationQuantity(false);

        final List<Move[]> movesets = new ArrayList<>(moveCombinations);
        for (int i = 0; i < moveCombinations; i++) {
            movesets.add(this.species.moveCombinationFromId(i, false));
        }

        this.battleResults = movesets.parallelStream()
            .map((moveset) -> {
                Creature playerCreature = this.creatureFactory.apply(moveset[0], getChargedMoves(moveset));
                BattleSolver battleSolver = battleSolverFactory.apply(playerCreature);
                battleSolver.solve();
                return battleSolver.getBattleResult();
            })
            .collect(Collectors.toCollection(ArrayList::new));
        
        this.battleResults.sort(Comparator.reverseOrder());
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
        if (this.battleResults.isEmpty()) return null;
        return this.battleResults.get(0);
    }

    public List<BattleResult> getBattleResults() {
        return this.battleResults;
    }
}
