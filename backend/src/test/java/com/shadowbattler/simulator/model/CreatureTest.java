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

    private Creature kyogre151513;
    private Creature gruntIvysaur;
    private Creature leaderAlakazam;

    @BeforeEach
    public void setup() {
        kyogre151513 = new Creature(
            speciesDataService.getSpeciesById("kyogre"), 
            new Stats3<>(15, 15, 13), 
            50.0, 
            movesDataService.getMoveById("WATERFALL"), 
            List.of(
                movesDataService.getMoveById("ORIGIN_PULSE"),
                movesDataService.getMoveById("SURF")
            )
        );

        gruntIvysaur = new Creature(
            speciesDataService.getSpeciesById("ivysaur_shadow"), 
            Opponent.Title.ROCKET_GRUNT, 
            69, 
            movesDataService.getMoveById("VINE_WHIP"), 
            movesDataService.getMoveById("POWER_WHIP")
        );

        leaderAlakazam = new Creature(
            speciesDataService.getSpeciesById("alakazam_shadow"), 
            Opponent.Title.ROCKET_LEADER, 
            70, 
            movesDataService.getMoveById("CONFUSION"), 
            movesDataService.getMoveById("PSYCHIC")
        );
    }

    @Test
    public void testStats() {
        final Stats3<Double> stats = kyogre151513.getStats();
        assertEquals(239.4, stats.getAtk(), 0.1);
        assertEquals(204.1, stats.getDef(), 0.1);
        assertEquals(183.0, stats.getHp(), 0.001);
    }

    @Test
    public void testCp() {
        assertEquals(4631, kyogre151513.getCp());
    }

    @Test
    public void testGruntCp() {
        assertEquals(5544, gruntIvysaur.getCp());
    }

    @Test
    public void testLeaderCp() {
        assertEquals(11100, leaderAlakazam.getCp());
    }
}
