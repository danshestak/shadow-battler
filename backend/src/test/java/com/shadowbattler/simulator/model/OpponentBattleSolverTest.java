package com.shadowbattler.simulator.model;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.shadowbattler.simulator.model.battle.OpponentBattleSolver;
import com.shadowbattler.simulator.service.MovesDataService;
import com.shadowbattler.simulator.service.OpponentDataService;
import com.shadowbattler.simulator.service.SpeciesDataService;

@SpringBootTest
public class OpponentBattleSolverTest {
    @Autowired
    MovesDataService movesDataService;
    @Autowired
    SpeciesDataService speciesDataService;
    @Autowired
    OpponentDataService opponentDataService;

    private OpponentBattleSolver helper(String playerSpeciesId, String playerFastId, String playerChargedId, String opponentId) {
        final Species playerSpecies = this.speciesDataService.getSpeciesById(playerSpeciesId);
        final Move playerFast = this.movesDataService.getMoveById(playerFastId);
        final Move playerCharged = movesDataService.getMoveById(playerChargedId);
        final Opponent opponent = this.opponentDataService.getOpponentById(opponentId);

        System.out.println(playerSpecies);
        System.out.println(playerFast);
        System.out.println(playerCharged);
        System.out.println(opponent);

        return new OpponentBattleSolver(
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
            opponent,
            70
        );
    }

    @Test
    void testSolve() {
        final OpponentBattleSolver lineupBattleSolver1 = helper(
            "magnezone_shadow", 
            "VOLT_SWITCH", 
            "WILD_CHARGE", 
            "grunt_f_water"
        );
        System.out.println(lineupBattleSolver1);
        lineupBattleSolver1.solve();

        final OpponentBattleSolver lineupBattleSolver2 = helper(
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
