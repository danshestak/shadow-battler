package com.shadowbattler.simulator.model.battle;

import com.shadowbattler.simulator.model.Creature;
import com.shadowbattler.simulator.model.Move;

/**
 * a wrapper for a creature that adds battling functionality, such as dealing and receiving damage
 * and processing in-battle stat changes. by separating this data into a wrapper, memory is able to 
 * be used more efficiently, as a copy constructor will keep a reference to the original creature 
 * rather than copying all of it's data
 */
public class BattlingCreature {
    final private Creature creature;
    private int remainingHp;
    private int energy;
    
    final protected static int MAX_BUFF_STAGES = 4;

    final private static double[] BUFF_MULTIPLIERS = {
        4.0 / 8.0, // -4
        4.0 / 7.0, // -3
        4.0 / 6.0, // -2
        4.0 / 5.0, // -1
        1.0, //  0
        5.0 / 4.0, // +1
        6.0 / 4.0, // +2
        7.0 / 4.0, // +3
        8.0 / 4.0  // +4
    };

    final private static double[] INVERSE_BUFF_MULTIPLIERS = {
        8.0 / 4.0, // -4
        7.0 / 4.0, // -3
        6.0 / 4.0, // -2
        5.0 / 4.0, // -1
        1.0,       //  0
        4.0 / 5.0, // +1
        4.0 / 6.0, // +2
        4.0 / 7.0, // +3
        4.0 / 8.0  // +4
    };

    public BattlingCreature(Creature creature) {
        this.creature = creature;
        this.remainingHp = (int)Math.round(creature.getStats().getHp());
        this.energy = 0;
    }

    public BattlingCreature(BattlingCreature other) {
        this.creature = other.creature;
        this.remainingHp = other.remainingHp;
        this.energy = other.energy;
    }

    public Creature getCreature() {
        return this.creature;
    }

    public int getRemainingHp() {
        return this.remainingHp;
    }

    public int getEnergy() {
        return this.energy;
    }

    public void adjustEnergy(int delta) {
        this.energy = Math.clamp(this.energy + delta, 0, 100);
    }

    public boolean isFainted() {
        return this.remainingHp <= 0;
    }

    public void processDamage(int damage) {
        this.remainingHp = Math.max(this.remainingHp - damage, 0);
    }
    
    public double calculateEffectiveAtk(int atkBuff) { 
        return this.creature.getBattleAtk() * BattlingCreature.BUFF_MULTIPLIERS[atkBuff + 4];
    }

    public double calculateEffectiveDef(int defBuff) {
        return this.creature.getBattleDef() * BattlingCreature.BUFF_MULTIPLIERS[defBuff + 4];
    }

    public double calculateInverseEffectiveDef(int defBuff) {
        return this.creature.getInverseBattleDef() * BattlingCreature.INVERSE_BUFF_MULTIPLIERS[defBuff + 4];
    }

    /**
     * calculates the damage this BattlingCreature does to the target Battling creature using the specified move
     * @param target the BattlingCreature being attacked
     * @param move the move being used on the target by the user
     * @param atkBuff the stage of attack buff the user has
     * @param targetDefBuff the stage of defense buff the user has
     * @return the damage this BattlingCreature can inflict
     */
    public int calculateDamageAgainst(BattlingCreature target, Move move, int atkBuff, int targetDefBuff) {
        /*
        for charged moves there is also a charge multiplier that goes from 0.25 - 1, however
        we will assume that the charged move is always fully charged
        */
        final double powerWithStab;
        if (move == this.creature.getFastMove()) {
            powerWithStab = this.creature.getFastMovePowerWithStab();
        } else if (move == this.creature.getChargedMoves().get(0)) {
            powerWithStab = this.creature.getChargedMovePowersWithStab()[0];
        } else {
            powerWithStab = this.creature.getChargedMovePowersWithStab()[1];
        }

        return 1 + (int)(
            powerWithStab *
            this.calculateEffectiveAtk(atkBuff) * target.calculateInverseEffectiveDef(targetDefBuff) *
            move.type().effectivenessAgainst(target.creature.getSpecies().getTypes())
        );
    }

    /**
     * uses calculateDamageAgainst to deal damage to the target using the specified move, adjusts 
     * energy according to the move's energy/energyGain, and applies buffs if they have high odds 
     * of occurring using processBuff. may faint target, check after use using isFainted method
     * @param target the BattlingCreature being attacked
     * @param move the move being used on the target by the user
     * @param atkBuff the stage of attack buff the user has
     * @param targetDefBuff the stage of defense buff the user has
     */
    public void attack(BattlingCreature target, Move move, int atkBuff, int targetDefBuff) {
        target.processDamage(this.calculateDamageAgainst(target, move, atkBuff, targetDefBuff));
        if (move.energy() > 0) { //charged move
            this.adjustEnergy(-move.energy());
        } else { //fast move
            this.adjustEnergy(move.energyGain());
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BattlingCreature{");
        sb.append("species=").append(creature.getSpecies());
        sb.append(", remainingHp=").append(remainingHp);
        sb.append(", energy=").append(energy);
        sb.append('}');
        return sb.toString();
    }
}
