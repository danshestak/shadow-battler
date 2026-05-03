package com.shadowbattler.simulator.model;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.shadowbattler.simulator.service.SpeciesDataService;

@SpringBootTest
public class SpeciesTest {
    @Autowired
    private SpeciesDataService speciesDataService;

    private void printMoveCombinationsFor(Species species) {
        for (boolean enemyMoves : List.of(true, false)) {
            for (int i = 0; i < species.moveCombinationQuantity(enemyMoves); i++) {
                System.out.printf(
                    "move combination %d (%s): %s\n",
                    i,
                    enemyMoves ? "enemy" : "player",
                    Arrays.asList(species.moveCombinationFromId(i, enemyMoves))
                );
            }
        }
    }

    @Test
    public void testMoveCombinationFromId() {
        printMoveCombinationsFor(speciesDataService.getSpeciesById("kyurem_white"));
    }

    @Test
    public void testMoveCombinationFromIdWithRequiredMove() {
        printMoveCombinationsFor(speciesDataService.getSpeciesById("zacian_crowned_sword"));
    }

    @Test
    public void testMoveCombinationFromIdWithoutThirdMove() {
        printMoveCombinationsFor(speciesDataService.getSpeciesById("smeargle"));
    }
}
