package com.shadowbattler.simulator.model;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.shadowbattler.simulator.model.battle.BattleSolver;
import com.shadowbattler.simulator.model.battle.BattleState;
import com.shadowbattler.simulator.service.MovesDataService;
import com.shadowbattler.simulator.service.SpeciesDataService;

@SpringBootTest
public class BattleSolverTest {
    @Autowired
    SpeciesDataService speciesDataService;
    @Autowired
    MovesDataService movesDataService;

    Creature pLead;
    Creature pSwitch;
    Creature pCloser;
    Creature eLead;
    Creature eSwitch;
    Creature eCloser;

    private void printBattle(BattleSolver battle) {
        for (BattleState state : battle.getFinishedStates()) {
            System.out.println(state);
            System.out.println();
        }
    }

    private Creature helper(String species, String fast, String charged1, String charged2) {
        final ArrayList<String> chargedIds = new ArrayList<>(){{add(charged1); add(charged2);}};

        return new Creature(
            this.speciesDataService.getSpeciesById(species), 
            Stats3.getMaxIVs(), 
            50.0, 
            this.movesDataService.getMoveById(fast),
            chargedIds.stream().filter(Objects::nonNull).map(this.movesDataService::getMoveById).toList()
        );
    }

    @BeforeEach
    public void setup() {
        this.pLead = helper("registeel", "LOCK_ON", "FLASH_CANNON", null);
        this.pSwitch = null; //helper("regice", "LOCK_ON", "BLIZZARD", null);
        this.pCloser = null; //helper("regirock", "LOCK_ON", "STONE_EDGE", null);
        this.eLead = helper("moltres", "FIRE_SPIN", "SKY_ATTACK", "ANCIENT_POWER");
        this.eSwitch = helper("zapdos", "THUNDER_SHOCK", "DRILL_PECK", "THUNDERBOLT");
        this.eCloser = helper("articuno", "ICE_SHARD", "TRIPLE_AXEL", "ANCIENT_POWER");
    }

    @Test
    void testStepAll() {
        final BattleSolver battle = new BattleSolver( 
            new Team<>(this.pLead, this.pSwitch, this.pCloser), 
            new Team<>(this.eLead, this.eSwitch, this.eCloser), 
            0
        );

        int steps = 0;
        while (battle.stepAll()) {
            System.out.printf("--- STEP %d ---\n\n", steps);
            System.out.printf(
                "there are %d states (%d active, %d finished)\n\n", 
                battle.getActiveStates().size() + battle.getFinishedStates().size(),
                battle.getActiveStates().size(),
                battle.getFinishedStates().size()
            );
            // printBattle(battle);

            steps++;
            // if (steps > 800) break;
        }

        int stateNumber = 0;
        for (BattleState state : battle.getFinishedStates()) {
            System.out.printf(
                "--- FINAL STATE (%s) %d ---\n",
                state.getPlayer().getRemainingCreatures() > 0 && state.getEnemy().getRemainingCreatures() <= 0 ? "WIN" :
                state.getPlayer().getRemainingCreatures() <= 0 ? "LOSS" :
                "DRAW/INCOMPLETE",
                stateNumber
            );
            // System.out.printf("finished: %s, time: %d, log: %s\n\n", state.isFinished(), state.getTimeElapsed(), state.getLog());
            stateNumber++;
        }

        final Optional<BattleState> fastest = battle.findFastestWin();
        System.out.printf("FASTEST: %s\n", fastest.isPresent() ? fastest.get() : "none");
        System.out.printf("%s\n", fastest.isPresent() ? fastest.get().getLog() : "");
    }
}
