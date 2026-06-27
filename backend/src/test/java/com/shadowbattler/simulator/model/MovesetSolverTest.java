package com.shadowbattler.simulator.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.shadowbattler.simulator.model.battle.MovesetSolver;
import com.shadowbattler.simulator.model.battle.OpponentBattleSolver;
import com.shadowbattler.simulator.service.OpponentDataService;
import com.shadowbattler.simulator.service.SpeciesDataService;

@SpringBootTest
public class MovesetSolverTest {
    @Autowired
    SpeciesDataService speciesDataService;
    @Autowired
    OpponentDataService opponentDataService;

    @Test
    void testGetMovesetScores() {
        MovesetSolver movesetSolver = new MovesetSolver(
            (fast, charged) -> new Creature(
                speciesDataService.getSpeciesById("charizard_shadow"), 
                Stats3.getMaxIVs(), 
                50, 
                fast, 
                charged
            ),
            (playerCreature) -> new OpponentBattleSolver(
                new Team<>(playerCreature, null, null), 
                opponentDataService.getOpponentById("grunt_f_fire"),
                80
            )
        );

        movesetSolver.solve();

        System.out.println(movesetSolver.getMovesetBattleResults());
    }
}
