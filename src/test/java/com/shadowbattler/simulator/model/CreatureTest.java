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
    }

    @Test
    public void testStats() {
        final Stats3<Double> stats = kyogre151513.getStats();
        assertEquals(stats.getAtk(), 239.4, 0.1);
        assertEquals(stats.getDef(), 204.1, 0.1);
        assertEquals(stats.getHp(), 183.0, 0.001);
    }

    @Test
    public void testCp() {
        assertEquals(kyogre151513.getCp(), 4631);
    }
}
