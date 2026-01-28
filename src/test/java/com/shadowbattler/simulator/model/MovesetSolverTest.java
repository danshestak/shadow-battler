package com.shadowbattler.simulator.model;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.shadowbattler.simulator.model.battle.LineupBattleSolver;
import com.shadowbattler.simulator.model.battle.MovesetSolver;
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
            speciesDataService.getSpeciesById("latios_shadow"),
            (playerCreature) -> new LineupBattleSolver(
                new Team<>(playerCreature, null, null), 
                opponentDataService.getOpponentById("leader_cliff")
            )
        );

        movesetSolver.solve();

        System.out.println(movesetSolver.getMovesetBattleResults());
    }
}
