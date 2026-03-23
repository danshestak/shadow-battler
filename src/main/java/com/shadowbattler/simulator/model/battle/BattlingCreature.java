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
    final private double baseAtk;
    final private double baseDef;

    final private static double BONUS_MULTIPLIER = 1.2999999523162841796875;
    final private static double STAB_MULTIPLIER= 1.2000000476837158203125;
    final private static double SHADOW_ATK_MULTIPLIER = 1.2;
    final private static double SHADOW_DEF_MULTIPLIER = 0.83333331;

    final private static double BUFF_DIVISOR = 4.0;
    final protected static int MAX_BUFF_STAGES = 4;

    public BattlingCreature(Creature creature) {
        this.creature = creature;
        this.remainingHp = (int)Math.round(creature.getStats().getHp());
        this.energy = 0;

        double atk = creature.getStats().getAtk();
        double def = creature.getStats().getDef();
        if (creature.getSpecies().isShadow()) {
            atk *= BattlingCreature.SHADOW_ATK_MULTIPLIER;
            def *= BattlingCreature.SHADOW_DEF_MULTIPLIER;
        }
        this.baseAtk = atk;
        this.baseDef = def;
    }

    public BattlingCreature(BattlingCreature other) {
        this.creature = other.creature;
        this.remainingHp = other.remainingHp;
        this.energy = other.energy;
        this.baseAtk = other.baseAtk;
        this.baseDef = other.baseDef;
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
        if (atkBuff == 0) return this.baseAtk;
        double multiplier = atkBuff > 0 ? (BattlingCreature.BUFF_DIVISOR + atkBuff) / BattlingCreature.BUFF_DIVISOR : BattlingCreature.BUFF_DIVISOR / (BattlingCreature.BUFF_DIVISOR - atkBuff);
        return this.baseAtk * multiplier;
    }

    public double calculateEffectiveDef(int defBuff) {
        if (defBuff == 0) return this.baseDef;
        double multiplier = defBuff > 0 ? (BattlingCreature.BUFF_DIVISOR + defBuff) / BattlingCreature.BUFF_DIVISOR : BattlingCreature.BUFF_DIVISOR / (BattlingCreature.BUFF_DIVISOR - defBuff);
        return this.baseDef * multiplier;
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
        return 1 + (int)Math.floor(
            move.power() *
            this.calculateEffectiveAtk(atkBuff)/target.calculateEffectiveDef(targetDefBuff) *
            move.type().effectivenessAgainst(target.creature.getSpecies().getTypes()) *
            (this.creature.getSpecies().givesStabTo(move) ? BattlingCreature.STAB_MULTIPLIER : 1) *
            0.5 *
            BattlingCreature.BONUS_MULTIPLIER
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
