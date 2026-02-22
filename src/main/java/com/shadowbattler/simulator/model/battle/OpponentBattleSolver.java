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
        final List<Team<Creature>> opponentTeamCombinations = new ArrayList<>();
        for (int lineupId = 0; lineupId < opponent.getLineupSpecies().combinationQuantity(); lineupId++) {
            final Team<Species> lineup = opponent.getLineupSpecies().combinationFromId(lineupId);
            
            for (int i = 0; i < lineup.getFirst().moveCombinationQuantity(true); i++) {
            for (int j = 0; j < lineup.getSecond().moveCombinationQuantity(true); j++) {
            for (int k = 0; k < lineup.getThird().moveCombinationQuantity(true); k++) {
                opponentTeamCombinations.add(
                    new Team<>(
                        creatureFromMoveCombinationId(lineup.getFirst(), this.opponent, i),
                        creatureFromMoveCombinationId(lineup.getSecond(), this.opponent, j),
                        creatureFromMoveCombinationId(lineup.getThird(), this.opponent, k)
                    )
                );
            }}}
        }
        
        final List<BattleResult> battleResults = opponentTeamCombinations.parallelStream()
            .map((opponentTeam) -> {
                final TeamBattleSolver teamBattleSolver = new TeamBattleSolver(
                    this.playerTeam,
                    opponentTeam,
                    opponent.getTitle().getShields()
                );
                teamBattleSolver.solve();
                return teamBattleSolver.getBattleResult();
            })
            .toList();
        this.battleResult = BattleResult.averageOf(battleResults);
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
