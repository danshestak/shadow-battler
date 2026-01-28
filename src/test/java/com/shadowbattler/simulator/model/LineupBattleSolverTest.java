package com.shadowbattler.simulator.model;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.shadowbattler.simulator.model.battle.LineupBattleSolver;
import com.shadowbattler.simulator.service.MovesDataService;
import com.shadowbattler.simulator.service.OpponentDataService;
import com.shadowbattler.simulator.service.SpeciesDataService;

@SpringBootTest
public class LineupBattleSolverTest {
    @Autowired
    MovesDataService movesDataService;
    @Autowired
    SpeciesDataService speciesDataService;
    @Autowired
    OpponentDataService opponentDataService;

    private LineupBattleSolver helper(String playerSpeciesId, String playerFastId, String playerChargedId, String opponentId) {
        final Species playerSpecies = this.speciesDataService.getSpeciesById(playerSpeciesId);
        final Move playerFast = this.movesDataService.getMoveById(playerFastId);
        final Move playerCharged = movesDataService.getMoveById(playerChargedId);
        final Opponent opponent = this.opponentDataService.getOpponentById(opponentId);

        System.out.println(playerSpecies);
        System.out.println(playerFast);
        System.out.println(playerCharged);
        System.out.println(opponent);

        return new LineupBattleSolver(
            new Team<>(
                new Creature(
                    playerSpecies, 
                    Stats3.getMaxIVs(), 
                    50, 
                    playerFast, 
                    List.of(playerCharged)
                ),
                null,
                null
            ),
            opponent
        );
    }

    @Test
    void testSolve() {
        final LineupBattleSolver lineupBattleSolver1 = helper(
            "magnezone_shadow", 
            "VOLT_SWITCH", 
            "WILD_CHARGE", 
            "grunt_f_water"
        );
        System.out.println(lineupBattleSolver1);
        lineupBattleSolver1.solve();

        final LineupBattleSolver lineupBattleSolver2 = helper(
            "rampardos", 
            "SMACK_DOWN", 
            "ROCK_SLIDE", 
            "grunt_f_water"
        );
        lineupBattleSolver2.solve();

        System.out.println(lineupBattleSolver1.getBattleResult());
        System.out.println(lineupBattleSolver2.getBattleResult());
    }
}
