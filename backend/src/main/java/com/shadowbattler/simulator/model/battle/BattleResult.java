package com.shadowbattler.simulator.model.battle;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.model.Opponent;
import com.shadowbattler.simulator.model.Species;

public class BattleResult implements Comparable<BattleResult> {
    private final int timeElapsed;
    private final double timeElapsedVariance;
    private final double winPercent;
    private final double hpPercent;
    @JsonIdentityReference(alwaysAsId = true)
    private final Move playerFastMove;
    @JsonIdentityReference(alwaysAsId = true)
    private final Move playerChargedMove1;
    @JsonIdentityReference(alwaysAsId = true)
    private final Move playerChargedMove2;
    @JsonIdentityReference(alwaysAsId = true)
    private final Species playerSpecies;
    @JsonIdentityReference(alwaysAsId = true)
    private final Opponent opponent;
    private final Double playerLevel;
    private final Integer trainerLevel;

    private static final double SCORE_CALCULATION_CONSTANT = 100000000.0;
    
    public BattleResult(
        int timeElapsed, 
        double winPercent, 
        double hpPercent,
        Move playerFastMove,
        Move playerChargedMove1,
        Move playerChargedMove2,
        Species playerSpecies,
        Opponent opponent,
        Double playerLevel,
        Integer trainerLevel
    ) {
        this.timeElapsed = timeElapsed;
        this.winPercent = winPercent;
        this.hpPercent = hpPercent;
        this.timeElapsedVariance = 0.0;
        this.playerFastMove = playerFastMove;
        this.playerChargedMove1 = playerChargedMove1;
        this.playerChargedMove2 = playerChargedMove2;
        this.playerSpecies = playerSpecies;
        this.opponent = opponent;
        this.playerLevel = playerLevel;
        this.trainerLevel = trainerLevel;
    }

    private BattleResult(
        int timeElapsed, 
        double winPercent, 
        double hpPercent, 
        double timeElapsedVariance,
        Move playerFastMove,
        Move playerChargedMove1,
        Move playerChargedMove2,
        Species playerSpecies,
        Opponent opponent,
        Double playerLevel,
        Integer trainerLevel
    ) {
        this.timeElapsed = timeElapsed;
        this.winPercent = winPercent;
        this.hpPercent = hpPercent;
        this.timeElapsedVariance = timeElapsedVariance;
        this.playerFastMove = playerFastMove;
        this.playerChargedMove1 = playerChargedMove1;
        this.playerChargedMove2 = playerChargedMove2;
        this.playerSpecies = playerSpecies;
        this.opponent = opponent;
        this.playerLevel = playerLevel;
        this.trainerLevel = trainerLevel;
    }

    public static BattleResult averageOf(List<BattleResult> battleResults, Opponent opponent) {
        final var first = !battleResults.isEmpty() ? battleResults.get(0) : null;

        double timeElapsedAvg = 0.0;
        double timeElapsedSquareAvg = 0.0;
        double winPercentAvg = 0.0;
        double hpPercentAvg = 0.0;
        
        int losses = 0;
        for (BattleResult battleResult : battleResults) {
            if (battleResult.isLoss()) {
                losses++;
                continue;
            }

            timeElapsedAvg += battleResult.timeElapsed;
            timeElapsedSquareAvg += Math.pow(battleResult.timeElapsed, 2.0);
            winPercentAvg += battleResult.winPercent;
            hpPercentAvg += battleResult.hpPercent;
        }

        if (!battleResults.isEmpty()) {
            timeElapsedAvg /= (battleResults.size() - losses);
            timeElapsedSquareAvg /= (battleResults.size() - losses);
            winPercentAvg /= battleResults.size();
            hpPercentAvg /= (battleResults.size() - losses);
        }
        
        return new BattleResult(
            (int)Math.round(timeElapsedAvg), 
            winPercentAvg, 
            hpPercentAvg, 
            Math.max(0, timeElapsedSquareAvg - Math.pow(timeElapsedAvg, 2.0)),
            first == null ? null : first.playerFastMove, 
            first == null ? null : first.playerChargedMove1, 
            first == null ? null : first.playerChargedMove2, 
            first == null ? null : first.playerSpecies, 
            opponent == null ? (first == null ? null : first.opponent) : opponent, 
            first == null ? null : first.playerLevel, 
            first == null ? null : first.trainerLevel
        );
    }

    @Override
    public int compareTo(BattleResult o) {
        if (o == null) return 1;
        return Integer.valueOf(this.getScore()).compareTo(o.getScore());
    }

    public int getTimeElapsed() {
        return this.timeElapsed;
    }

    public double getTimeElapsedVariance() {
        return this.timeElapsedVariance;
    }

    public double getWinPercent() {
        return this.winPercent;
    }

    public double getHpPercent() {
        return this.hpPercent;
    }

    public int getScore() {
        if (this.timeElapsed <= 0.0 || this.winPercent <= 0.0) return 0;
        return (int)(BattleResult.SCORE_CALCULATION_CONSTANT/this.timeElapsed * this.winPercent);
    }

    @JsonIgnore
    public boolean isLoss() {
        return this.winPercent == 0.0;
    }

    @JsonIgnore
    public Optional<Move> getPlayerFastMove() {
        return Optional.ofNullable(this.playerFastMove);
    }

    @JsonProperty("playerFastMove")
    @SuppressWarnings("unused")
    private String getPlayerFastMoveId() {
        return this.playerFastMove == null ? null : this.playerFastMove.moveId();
    }

    @JsonIgnore
    public Optional<Move> getPlayerChargedMove1() {
        return Optional.ofNullable(this.playerChargedMove1);
    }

    @JsonProperty("playerChargedMove1")
    @SuppressWarnings("unused")
    private String getPlayerChargedMove1Id() {
        return this.playerChargedMove1 == null ? null : this.playerChargedMove1.moveId();
    }

    @JsonIgnore
    public Optional<Move> getPlayerChargedMove2() {
        return Optional.ofNullable(this.playerChargedMove2);
    }

    @JsonProperty("playerChargedMove2")
    @SuppressWarnings("unused")
    private String getPlayerChargedMove2Id() {
        return this.playerChargedMove2 == null ? null : this.playerChargedMove2.moveId();
    }

    @JsonIgnore
    public Move[] getMoveset() {
        return new Move[]{this.playerFastMove, this.playerChargedMove1, this.playerChargedMove2};
    }

    @JsonIgnore
    public Optional<Species> getPlayerSpecies() {
        return Optional.ofNullable(this.playerSpecies);
    }

    @JsonProperty("playerSpecies")
    @SuppressWarnings("unused")
    private String getPlayerSpeciesId() {
        return this.playerSpecies == null ? null : this.playerSpecies.getSpeciesId();
    }

    @JsonIgnore
    public Optional<Opponent> getOpponent() {
        return Optional.ofNullable(this.opponent);
    }
    
    @JsonProperty("opponent")
    @SuppressWarnings("unused")
    private String getOpponentId() {
        return this.opponent == null ? null : this.opponent.getOpponentId();
    }

    public Optional<Double> getPlayerLevel() {
        return Optional.ofNullable(this.playerLevel);
    }

    public Optional<Integer> getTrainerLevel() {
        return Optional.ofNullable(this.trainerLevel);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BattleResult{");
        sb.append("timeElapsed=").append(timeElapsed);
        sb.append(", timeElapsedVariance=").append(timeElapsedVariance);
        sb.append(", winPercent=").append(winPercent);
        sb.append(", hpPercent=").append(hpPercent);
        sb.append(", playerFastMove=").append(playerFastMove);
        sb.append(", playerChargedMove1=").append(playerChargedMove1);
        sb.append(", playerChargedMove2=").append(playerChargedMove2);
        sb.append(", playerSpecies=").append(playerSpecies);
        sb.append(", opponent=").append(opponent);
        sb.append(", playerLevel=").append(playerLevel);
        sb.append(", trainerLevel=").append(trainerLevel);
        sb.append('}');
        return sb.toString();
    }
}
