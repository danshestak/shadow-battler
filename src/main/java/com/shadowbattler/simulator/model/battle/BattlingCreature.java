package com.shadowbattler.simulator.model.battle;

import com.shadowbattler.simulator.model.Creature;
import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.model.Stats3;

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
    
    private double calculateEffectiveStat(Stats3.Stat stat, int buff) {
        double multiplier;

        if (buff > 0) {
            multiplier = (BattlingCreature.BUFF_DIVISOR + buff) / BattlingCreature.BUFF_DIVISOR;
        } else if (buff < 0) {
            multiplier = BattlingCreature.BUFF_DIVISOR / (BattlingCreature.BUFF_DIVISOR - buff);
        } else {
            multiplier = 1.0;
        }

        if (this.creature.getSpecies().isShadow()) {
            multiplier *= stat == Stats3.Stat.ATK ? BattlingCreature.SHADOW_ATK_MULTIPLIER : BattlingCreature.SHADOW_DEF_MULTIPLIER;
        }

        return this.creature.getStats().getByEnum(stat) * multiplier;
    }

    public double calculateEffectiveAtk(int atkBuff) { 
        return this.calculateEffectiveStat(Stats3.Stat.ATK, atkBuff);
    }

    public double calculateEffectiveDef(int defBuff) {
        return this.calculateEffectiveStat(Stats3.Stat.DEF, defBuff); 
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
