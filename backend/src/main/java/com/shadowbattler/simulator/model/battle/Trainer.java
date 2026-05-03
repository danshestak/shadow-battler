package com.shadowbattler.simulator.model.battle;

import com.shadowbattler.simulator.model.Creature;
import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.model.Stats3;
import com.shadowbattler.simulator.model.Team;

/**
 * class that is used in battles to represent either the player or the enemy. not to be
 * confused with the Opponent class, which is immutable and used for storing data about
 * go rocket/team leader lineups
 */
public class Trainer {
    final private Team<BattlingCreature> team;
    private int activeSlot;
    private int shields;
    private Action queuedAction;
    private int queuedActionFulfills;
    private int switchCooldownEnds;
    private int remainingCreatures;
    private boolean stunQueued; //for enemy on switch and charged attack
    /*
    since buffs only apply to the active creature, we are associating them with the trainer
    rather than the creatures themselves to save memory
    */
    private int atkBuff;
    private int defBuff;
    
    public Trainer(Team<Creature> team, int shields) {
        this.team = new Team<>(
            team.getFirst() != null ? new BattlingCreature(team.getFirst()) : null,
            team.getSecond() != null ? new BattlingCreature(team.getSecond()) : null,
            team.getThird() != null ? new BattlingCreature(team.getThird()) : null
        );
        this.activeSlot = 1;
        this.shields = shields;
        this.queuedAction = null;
        this.queuedActionFulfills = 0;
        this.switchCooldownEnds = 0;
        this.remainingCreatures = this.team.size();
        this.stunQueued = false;
        this.atkBuff = 0;
        this.defBuff = 0;
    }

    public Trainer(Trainer other) {
        this.team = new Team<>(
            other.team.getFirst() != null ? new BattlingCreature(other.team.getFirst()) : null,
            other.team.getSecond() != null ? new BattlingCreature(other.team.getSecond()) : null,
            other.team.getThird() != null ? new BattlingCreature(other.team.getThird()) : null
        );
        this.activeSlot = other.activeSlot;
        this.shields = other.shields;
        this.queuedAction = other.queuedAction;
        this.queuedActionFulfills = other.queuedActionFulfills;
        this.switchCooldownEnds = other.switchCooldownEnds;
        this.remainingCreatures = other.remainingCreatures;
        this.stunQueued = other.stunQueued;
        this.atkBuff = other.atkBuff;
        this.defBuff = other.defBuff;
    }

    public Team<BattlingCreature> getTeam() {
        return this.team;
    }

    public int getActiveSlot() {
        return this.activeSlot;
    }

    public void setActiveSlot(int activeSlot) {
        this.activeSlot = activeSlot;
    }

    public BattlingCreature getActive() {
        return this.team.getByInt(this.activeSlot);
    }

    public int getShields() {
        return this.shields;
    }

    public void adjustShields(int delta) {
        this.shields += delta;
    }

    public Action getQueuedAction() {
        return this.queuedAction;
    }

    public void setQueuedAction(Action queuedAction) {
        this.queuedAction = queuedAction;
    }

    public int getQueuedActionFulfills() {
        return this.queuedActionFulfills;
    }

    public void setQueuedActionFulfills(int queuedActionFulfills) {
        this.queuedActionFulfills = queuedActionFulfills;
    }

    public int getSwitchCooldownEnds() {
        return this.switchCooldownEnds;
    }

    public void setSwitchCooldownEnds(int switchCooldownEnds) {
        this.switchCooldownEnds = switchCooldownEnds;
    }

    public int getRemainingCreatures() {
        return this.remainingCreatures;
    }

    public void adjustRemainingCreatures(int delta) {
        this.remainingCreatures += delta;
    }

    public boolean hasStunQueued() {
        return this.stunQueued;
    }

    public void setStunQueued(boolean stunQueued) {
        this.stunQueued = stunQueued;
    }

    public int getAtkBuff() {
        return this.atkBuff;
    }

    public void setAtkBuff(int atkBuff) {
        this.atkBuff = atkBuff;
    }

    public int getDefBuff() {
        return this.defBuff;
    }

    public void setDefBuff(int defBuff) {
        this.defBuff = defBuff;
    }

    public void processBuff(Stats3<Integer> buff) {
        if (buff == null) return;
        this.atkBuff = Math.clamp(this.atkBuff + buff.getAtk(), -BattlingCreature.MAX_BUFF_STAGES, BattlingCreature.MAX_BUFF_STAGES);
        this.defBuff = Math.clamp(this.defBuff + buff.getDef(), -BattlingCreature.MAX_BUFF_STAGES, BattlingCreature.MAX_BUFF_STAGES);
    }

    /**
     * applies buffs if their odds are above a specific value to remove randomness from simulations.
     * realistically, this only eliminates buffs that aren't guaranteed, as all moves with random 
     * buffs have odds at or below 0.5
     * @param move the move this Trainer's active is using
     * @param target the target of the move's Trainer
     */
    public void applyMoveBuffsChance(Move move, Trainer target) {
        if (move.buffApplyChance() > 0.8) {
            this.processBuff(move.buffsSelf());
            target.processBuff(move.buffsOpponent());
        }
    }

    public void switchTo(int slot) {
        if (this.getTeam().getByInt(slot).isFainted()) throw new IllegalArgumentException(
            String.format("attempted to switch to slot %n, which is fainted", slot)
        );
        this.setActiveSlot(slot);
        this.atkBuff = 0;
        this.defBuff = 0;
    }

    @Override
    public String toString() {
        return "Trainer [team=" + team + ", activeSlot=" + activeSlot + ", shields=" + shields + ", queuedAction="
                + queuedAction + ", queuedActionFulfills=" + queuedActionFulfills + ", switchCooldownEnds="
                + switchCooldownEnds + ", remainingCreatures=" + remainingCreatures + ", stunQueued=" + stunQueued
                + ", atkBuff=" + atkBuff + ", defBuff=" + defBuff + "]";
    }
}