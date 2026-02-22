package com.shadowbattler.simulator.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "battle_results")
/**
 * jpa entity for BattleResults. differs from the BattleResult class significantly to include
 * data about player creature and opponent
 */
public class BattleResultEntity {
    @Id
    @GeneratedValue
    private int id;
    private int timeElapsed;
    private double timeElapsedVariance;
    private double winPercent;
    private double hpPercent;
    private int score;
    @ManyToOne
    @JoinColumn(name = "player_fast_move_id", referencedColumnName = "moveId")
    private MoveEntity playerFastMove;
    @ManyToOne
    @JoinColumn(name = "player_charged_move_1_id", referencedColumnName = "moveId")
    private MoveEntity playerChargedMove1;
    @ManyToOne
    @JoinColumn(name = "player_charged_move_2_id", referencedColumnName = "moveId")
    private MoveEntity playerChargedMove2;
    @ManyToOne
    @JoinColumn(name = "speciesId", referencedColumnName = "speciesId")
    private SpeciesEntity playerSpecies;
    @ManyToOne
    @JoinColumn(name = "opponentId", referencedColumnName = "opponentId")
    private OpponentEntity opponent;
    //null for great/ultra league team leaders
    private Integer playerLevel;
    //null for team leaders since only rocket battles consider trainer level
    private Integer trainerLevel;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTimeElapsed() {
        return this.timeElapsed;
    }

    public void setTimeElapsed(int timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public double getTimeElapsedVariance() {
        return this.timeElapsedVariance;
    }

    public void setTimeElapsedVariance(double timeElapsedVariance) {
        this.timeElapsedVariance = timeElapsedVariance;
    }

    public double getWinPercent() {
        return this.winPercent;
    }

    public void setWinPercent(double winPercent) {
        this.winPercent = winPercent;
    }

    public double getHpPercent() {
        return this.hpPercent;
    }

    public void setHpPercent(double hpPercent) {
        this.hpPercent = hpPercent;
    }

    public MoveEntity getPlayerFastMove() {
        return playerFastMove;
    }

    public void setPlayerFastMove(MoveEntity playerFastMove) {
        this.playerFastMove = playerFastMove;
    }

    public MoveEntity getPlayerChargedMove1() {
        return playerChargedMove1;
    }

    public void setPlayerChargedMove1(MoveEntity playerChargedMove1) {
        this.playerChargedMove1 = playerChargedMove1;
    }

    public MoveEntity getPlayerChargedMove2() {
        return playerChargedMove2;
    }

    public void setPlayerChargedMove2(MoveEntity playerChargedMove2) {
        this.playerChargedMove2 = playerChargedMove2;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public SpeciesEntity getPlayerSpecies() {
        return this.playerSpecies;
    }

    public void setPlayerSpecies(SpeciesEntity playerSpecies) {
        this.playerSpecies = playerSpecies;
    }

    public OpponentEntity getOpponent() {
        return this.opponent;
    }

    public void setOpponent(OpponentEntity opponent) {
        this.opponent = opponent;
    }

    public Integer getPlayerLevel() {
        return this.playerLevel;
    }

    public void setPlayerLevel(Integer playerLevel) {
        this.playerLevel = playerLevel;
    }

    public Integer getTrainerLevel() {
        return this.trainerLevel;
    }

    public void setTrainerLevel(Integer trainerLevel) {
        this.trainerLevel = trainerLevel;
    }
}
