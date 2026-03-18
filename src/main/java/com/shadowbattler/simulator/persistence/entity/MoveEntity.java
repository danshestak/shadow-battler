package com.shadowbattler.simulator.persistence.entity;

import java.util.List;

import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.model.Stats3;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
    @OneToMany(mappedBy = "playerFastMove", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BattleResultEntity> battleResultsAsFastMove;
    @OneToMany(mappedBy = "playerChargedMove1", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BattleResultEntity> battleResultsAsChargedMove1;
    @OneToMany(mappedBy = "playerChargedMove2", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BattleResultEntity> battleResultsAsChargedMove2;

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

    public List<BattleResultEntity> getBattleResultsAsFastMove() {
        return this.battleResultsAsFastMove;
    }

    public void setBattleResultsAsFastMove(List<BattleResultEntity> battleResultsAsFastMove) {
        this.battleResultsAsFastMove = battleResultsAsFastMove;
    }

    public List<BattleResultEntity> getBattleResultsAsChargedMove1() {
        return this.battleResultsAsChargedMove1;
    }

    public void setBattleResultsAsChargedMove1(List<BattleResultEntity> battleResultsAsChargedMove1) {
        this.battleResultsAsChargedMove1 = battleResultsAsChargedMove1;
    }

    public List<BattleResultEntity> getBattleResultsAsChargedMove2() {
        return this.battleResultsAsChargedMove2;
    }

    public void setBattleResultsAsChargedMove2(List<BattleResultEntity> battleResultsAsChargedMove2) {
        this.battleResultsAsChargedMove2 = battleResultsAsChargedMove2;
    }

    /**
     * @param move a move to compare to
     * @return true iff all of this entity's attributes match those of the given move
     */
    public boolean representsMove(Move move) {
        if (!this.moveId.equals(move.moveId())) return false;
        if (this.power != move.power()) return false;
        if (this.energy != move.energy()) return false;
        if (this.energyGain != move.energyGain()) return false;
        if (this.turns != move.turns()) return false;
        if (!this.buffsSelf.equals(move.buffsSelf())) return false;
        if (!this.buffsOpponent.equals(move.buffsOpponent())) return false;
        return this.buffApplyChance == move.buffApplyChance();
    }
}
