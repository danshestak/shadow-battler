package com.shadowbattler.simulator.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.shadowbattler.simulator.service.MovesDataService;
import com.shadowbattler.simulator.service.SpeciesDataService;

@SpringBootTest
public class Battle_BattlingCreatureTest {
    @Autowired
    SpeciesDataService speciesDataService;
    @Autowired
    MovesDataService movesDataService;

    Battle.BattlingCreature v;
    Battle.BattlingCreature c;
    Battle.BattlingCreature s;

    Move bs;
    Move bb;
    Move fp;
    Move sp;

    private Battle.BattlingCreature helper(String species) {
        final Creature c = new Creature(
            this.speciesDataService.getSpeciesById(species), 
            Stats3.getMaxIVs(), 
            50.0, 
            null,
            null
        );
        return new Battle.BattlingCreature(c);
    }

    @BeforeEach
    public void setup() {
        v = helper("venusaur");
        c = helper("charizard");
        s = helper("snorlax_shadow");

        bs = this.movesDataService.getMoveById("BODY_SLAM");
        bb = this.movesDataService.getMoveById("BLAST_BURN");
        fp = this.movesDataService.getMoveById("FRENZY_PLANT");
        sp = this.movesDataService.getMoveById("SUPER_POWER");
    }

    @Test
    void testCalculateDamageAgainst_superEffectiveSTAB() {
        assertEquals(161, (int)this.c.calculateDamageAgainst(this.v, this.bb));
    }

    @Test
    void testCalculateDamageAgainst_fromShadowNotVeryEffective() {
        assertEquals(46, (int)this.s.calculateDamageAgainst(this.c, this.sp));
    }

    @Test 
    void testCalculateDamageAgainst_neutralToShadow() {
        assertEquals(56, (int)this.c.calculateDamageAgainst(this.s, this.bs));
    }
}
