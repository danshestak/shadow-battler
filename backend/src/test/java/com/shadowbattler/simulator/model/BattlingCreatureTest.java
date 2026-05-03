package com.shadowbattler.simulator.model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.shadowbattler.simulator.model.battle.BattlingCreature;
import com.shadowbattler.simulator.service.MovesDataService;
import com.shadowbattler.simulator.service.SpeciesDataService;

@SpringBootTest
public class BattlingCreatureTest {
    @Autowired
    SpeciesDataService speciesDataService;
    @Autowired
    MovesDataService movesDataService;

    BattlingCreature v;
    BattlingCreature c;
    BattlingCreature s;

    Move bs;
    Move bb;
    Move fp;
    Move sp;

    private BattlingCreature helper(String species, Move fast, List<Move> charged) {
        return new BattlingCreature(
            new Creature(
                this.speciesDataService.getSpeciesById(species), 
                Stats3.getMaxIVs(), 
                50.0, 
                fast,
                charged
            )
        );
    }

    @BeforeEach
    public void setup() {
        this.bs = this.movesDataService.getMoveById("BODY_SLAM");
        this.bb = this.movesDataService.getMoveById("BLAST_BURN");
        this.fp = this.movesDataService.getMoveById("FRENZY_PLANT");
        this.sp = this.movesDataService.getMoveById("SUPER_POWER");

        this.v = helper("venusaur", null, List.of(this.fp));
        this.c = helper("charizard", null, List.of(this.bb, this.bs));
        this.s = helper("snorlax_shadow", null, List.of(this.sp));
    }

    @Test
    void testCalculateDamageAgainst_superEffectiveSTAB() {
        assertEquals(161, (int)this.c.calculateDamageAgainst(this.v, this.bb, 0, 0));
    }

    @Test
    void testCalculateDamageAgainst_fromShadowNotVeryEffective() {
        assertEquals(46, (int)this.s.calculateDamageAgainst(this.c, this.sp, 0, 0));
    }

    @Test 
    void testCalculateDamageAgainst_neutralToShadow() {
        assertEquals(56, (int)this.c.calculateDamageAgainst(this.s, this.bs, 0, 0));
    }
    
    @Test 
    void testCalculateDamageAgainst_doubleResistedStab() {
        assertEquals(35, (int)this.v.calculateDamageAgainst(this.c, this.fp, 0, 0));
    }
}
