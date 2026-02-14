package com.shadowbattler.simulator.persistence.entity;

import com.shadowbattler.simulator.model.Stats3;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "moves")
public class MoveEntity {
    @Id
    private String moveId;
    private int power;
    private int energy;
    private int energyGain;
    private int turns;
    @Convert(converter = Stats3IntegerConverter.class)
    @Column(columnDefinition = "TEXT")
    private Stats3<Integer> buffsSelf;
    @Convert(converter = Stats3IntegerConverter.class)
    @Column(columnDefinition = "TEXT")
    private Stats3<Integer> buffsOpponent;
    private double buffApplyChance;

    public String getMoveId() {
        return this.moveId;
    }

    public void setMoveId(String moveId) {
        this.moveId = moveId;
    }

    public int getPower() {
        return this.power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getEnergy() {
        return this.energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getEnergyGain() {
        return this.energyGain;
    }

    public void setEnergyGain(int energyGain) {
        this.energyGain = energyGain;
    }

    public int getTurns() {
        return this.turns;
    }

    public void setTurns(int turns) {
        this.turns = turns;
    }

    public Stats3<Integer> getBuffsSelf() {
        return this.buffsSelf;
    }

    public void setBuffsSelf(Stats3<Integer> buffsSelf) {
        this.buffsSelf = buffsSelf;
    }

    public Stats3<Integer> getBuffsOpponent() {
        return this.buffsOpponent;
    }

    public void setBuffsOpponent(Stats3<Integer> buffsOpponent) {
        this.buffsOpponent = buffsOpponent;
    }

    public double getBuffApplyChance() {
        return this.buffApplyChance;
    }

    public void setBuffApplyChance(double buffApplyChance) {
        this.buffApplyChance = buffApplyChance;
    }
}
