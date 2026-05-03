package com.shadowbattler.simulator.model.battle;

import java.util.ArrayList;
import java.util.List;

import com.shadowbattler.simulator.model.Creature;
import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.model.Opponent;
import com.shadowbattler.simulator.model.Species;
import com.shadowbattler.simulator.model.Stats3;
import com.shadowbattler.simulator.model.Team;

public class OpponentBattleSolver implements BattleSolver {
    private BattleResult battleResult = null;
    private final Team<Creature> playerTeam;
    private final Opponent opponent;
    private final int trainerLevel;

    /**
     * constructor for OpponentBattleSolver when the opponent is a rocket member
     * @param playerTeam team of creatures that the player uses
     * @param opponent the opponent the player is battling. should be a rocket member
     * @param trainerLevel the trainer level. affects the stats of rocket opponents' creatures
     */
    public OpponentBattleSolver(Team<Creature> playerTeam, Opponent opponent, int trainerLevel) {
        this.playerTeam = playerTeam;
        this.opponent = opponent;
        this.trainerLevel = trainerLevel;
    }

    @Override
    public void solve() {
        final int lineupCombinationQty = opponent.getLineupSpecies().combinationQuantity();
        final List<Team<Creature>> teams = new ArrayList<>();

        for (int lineupId = 0; lineupId < lineupCombinationQty; lineupId++) {
            final Team<Species> lineup = opponent.getLineupSpecies().combinationFromId(lineupId);

            final List<Creature> firstSlotCreatures = getCreaturesForSlot(lineup.getFirst());
            final List<Creature> secondSlotCreatures = getCreaturesForSlot(lineup.getSecond());
            final List<Creature> thirdSlotCreatures = getCreaturesForSlot(lineup.getThird());

            for (Creature first : firstSlotCreatures) {
                for (Creature second : secondSlotCreatures) {
                    for (Creature third : thirdSlotCreatures) {
                        teams.add(new Team<>(first, second, third));
                    }
                }
            }
        }

        final List<BattleResult> battleResults = teams.parallelStream()
            .map(t -> {
                final TeamBattleSolver teamBattleSolver = new TeamBattleSolver(
                    this.playerTeam,
                    t,
                    opponent.getTitle().getShields()
                );
                teamBattleSolver.solve();
                return teamBattleSolver.getBattleResult();
            })
            .toList();

        this.battleResult = BattleResult.averageOf(battleResults);
    }
    
    private List<Creature> getCreaturesForSlot(Species species) {
        if (species == null) {
            return List.of((Creature) null);
        }

        final int combinationsQuantity = species.moveCombinationQuantity(true);
        final List<Creature> creatures = new ArrayList<>(combinationsQuantity);
        for (int i = 0; i < combinationsQuantity; i++) {
            creatures.add(creatureFromMoveCombinationId(species, this.opponent, i));
        }
        return creatures;
    }

    private Creature creatureFromMoveCombinationId(Species species, Opponent opponent, int moveCombinationId) {
        final Move[] moveCombination = species.moveCombinationFromId(moveCombinationId, true);

        if (opponent.getTitle().isRocket()) {
            return new Creature(
                species, 
                opponent.getTitle(), 
                this.trainerLevel, 
                moveCombination[0], 
                moveCombination[1]
            );
        } else {
            return new Creature(
                species, 
                Stats3.getMaxIVs(), 
                55, 
                moveCombination[0], 
                List.of(moveCombination[1])
            );
        }
    }

    @Override
    public BattleResult getBattleResult() {
        return this.battleResult;
    }
}
