package com.shadowbattler.simulator.model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.shadowbattler.simulator.service.MovesDataService;
import com.shadowbattler.simulator.service.SpeciesDataService;

@SpringBootTest
public class CreatureTest {
    @Autowired
    private SpeciesDataService speciesDataService;
    @Autowired
    private MovesDataService movesDataService;

    private Creature c1;
    private Creature c2;
    private Creature c3;

    @BeforeEach
    public void setup() {
        c1 = new Creature(
            speciesDataService.getSpeciesById("blastoise"), 
            new Stats3<>(15, 15, 15), 
            50.0, 
            movesDataService.getMoveById("WATERFALL"), 
            List.of(
                movesDataService.getMoveById("ORIGIN_PULSE"),
                movesDataService.getMoveById("SURF")
            )
        );

        c2 = new Creature(
            speciesDataService.getSpeciesById("ivysaur_shadow"), 
            Opponent.Title.ROCKET_GRUNT, 
            69, 
            movesDataService.getMoveById("VINE_WHIP"), 
            movesDataService.getMoveById("POWER_WHIP")
        );

        c3 = new Creature(
            speciesDataService.getSpeciesById("charizard_shadow"), 
            Opponent.Title.ROCKET_LEADER, 
            41, 
            movesDataService.getMoveById("CONFUSION"), 
            movesDataService.getMoveById("PSYCHIC")
        );
    }

    @Test
    public void testStats() {
        final Stats3<Double> stats = c1.getStats();
        assertEquals(156.2, stats.getAtk(), 0.1);
        assertEquals(186.5, stats.getDef(), 0.1);
        assertEquals(170.0, stats.getHp(), 0.001);
    }

    @Test
    public void testCp() {
        assertEquals(2788, c1.getCp());
    }

    @Test
    public void testGruntCp() {
        assertEquals(5544, c2.getCp());
    }

    @Test
    public void testLeaderCp() {
        assertEquals(6903, c3.getCp());
    }
}
