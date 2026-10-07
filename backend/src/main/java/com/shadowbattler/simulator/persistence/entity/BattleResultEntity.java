package com.shadowbattler.simulator.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.model.battle.BattleResult;
import com.shadowbattler.simulator.persistence.service.MoveEntityService;
import com.shadowbattler.simulator.persistence.service.OpponentEntityService;
import com.shadowbattler.simulator.persistence.service.SpeciesEntityService;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "battle_results")
public class BattleResultEntity {
    @Id
    @GeneratedValue
    @JsonIgnore
    private int id;
    private int timeElapsed;
    private double timeElapsedVariance;
    private double winPercent;
    private double hpPercent;
    private int score;
    @ManyToOne
    @JsonProperty("playerFastMoveId")
    @JoinColumn(name = "player_fast_move_id", referencedColumnName = "moveId")
    @JsonIdentityReference(alwaysAsId = true)
    private MoveEntity playerFastMove;
    @ManyToOne
    @JsonProperty("playerChargedMove1Id")
    @JoinColumn(name = "player_charged_move_1_id", referencedColumnName = "moveId")
    @JsonIdentityReference(alwaysAsId = true)
    private MoveEntity playerChargedMove1;
    @ManyToOne
    @JsonProperty("playerChargedMove2Id")
    @JoinColumn(name = "player_charged_move_2_id", referencedColumnName = "moveId")
    @JsonIdentityReference(alwaysAsId = true)
    private MoveEntity playerChargedMove2;
    @ManyToOne
    @JsonProperty("playerSpeciesId")
    @JoinColumn(name = "player_species_id", referencedColumnName = "speciesId")
    @JsonIdentityReference(alwaysAsId = true)
    private SpeciesEntity playerSpecies;
    @ManyToOne
    @JsonProperty("opponentId")
    @JoinColumn(name = "opponent_id", referencedColumnName = "opponentId")
    @JsonIdentityReference(alwaysAsId = true)
    private OpponentEntity opponent;
    //null for great/ultra league team leaders
    private Double playerLevel;
    //null for team leaders since only rocket battles consider trainer level
    private Integer trainerLevel;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getTimeElapsed() {
        return this.timeElapsed;
    }

    public void setTimeElapsed(Integer timeElapsed) {
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

    public Double getPlayerLevel() {
        return this.playerLevel;
    }

    public void setPlayerLevel(Double playerLevel) {
        this.playerLevel = playerLevel;
    }

    public Integer getTrainerLevel() {
        return this.trainerLevel;
    }

    public void setTrainerLevel(Integer trainerLevel) {
        this.trainerLevel = trainerLevel;
    }

    public void updateFromBattleResult(
        BattleResult battleResult,
        SpeciesEntityService speciesEntityService,
        OpponentEntityService opponentEntityService,
        MoveEntityService moveEntityService
    ) {
        this.setTimeElapsed(battleResult.getTimeElapsed());
        this.setTimeElapsedVariance(battleResult.getTimeElapsedVariance());
        this.setWinPercent(battleResult.getWinPercent());
        this.setHpPercent(battleResult.getHpPercent());
        this.setScore(battleResult.getScore());

        battleResult.getPlayerSpecies().ifPresent(s -> this.setPlayerSpecies(speciesEntityService.getReferenceById(s.getSpeciesId())));
        battleResult.getOpponent().ifPresent(o -> this.setOpponent(opponentEntityService.getReferenceById(o.getOpponentId())));

        Move[] moveset = battleResult.getMoveset();
        if (moveset[0] != null) this.setPlayerFastMove(moveEntityService.getReferenceById(moveset[0].moveId()));
        if (moveset[1] != null) this.setPlayerChargedMove1(moveEntityService.getReferenceById(moveset[1].moveId()));
        if (moveset[2] != null) this.setPlayerChargedMove2(moveEntityService.getReferenceById(moveset[2].moveId()));

        this.setPlayerLevel(battleResult.getPlayerLevel().orElse(null));
        this.setTrainerLevel(battleResult.getTrainerLevel().orElse(null));
    }
}
