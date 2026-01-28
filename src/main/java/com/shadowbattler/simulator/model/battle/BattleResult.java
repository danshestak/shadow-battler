package com.shadowbattler.simulator.model.battle;

import java.util.List;
import java.util.Optional;

public class BattleResult {
    private final Integer timeElapsed;
    private final double timeElapsedVariance;
    private final double winPercent;
    private final double hpPercent;
    private final int score;

    private static final double SCORE_CALCULATION_CONSTANT = 100000000.0;

    private static final BattleResult loss = new BattleResult(null, 0.0, 0.0);
    
    public BattleResult(Integer timeElapsed, double winPercent, double hpPercent) {
        this.timeElapsed = timeElapsed;
        this.winPercent = winPercent;
        this.hpPercent = hpPercent;
        this.score = this.calculateScore();
        this.timeElapsedVariance = 0.0;
    }

    private BattleResult(Integer timeElapsed, double winPercent, double hpPercent, double timeElapsedVariance) {
        this.timeElapsed = timeElapsed;
        this.winPercent = winPercent;
        this.hpPercent = hpPercent;
        this.score = this.calculateScore();
        this.timeElapsedVariance = timeElapsedVariance;
    }

    public static BattleResult averageOf(List<BattleResult> battleResults) {
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

            timeElapsedAvg += battleResult.getTimeElapsed().get();
            timeElapsedSquareAvg += Math.pow(battleResult.getTimeElapsed().get(), 2.0);
            winPercentAvg += battleResult.getWinPercent();
            hpPercentAvg += battleResult.getHpPercent();
        }
        if (losses == battleResults.size()) return BattleResult.loss;

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
            Math.max(0, timeElapsedSquareAvg - Math.pow(timeElapsedAvg, 2.0))
        );
    }

    private int calculateScore() {
        if (this.timeElapsed == null || this.winPercent == 0.0) return 0;
        
        return (int)(BattleResult.SCORE_CALCULATION_CONSTANT/this.timeElapsed * this.winPercent);
    }
    
    /**
     * @return integer if the result is a win, null otherwise
     */
    public Optional<Integer> getTimeElapsed() {
        return Optional.ofNullable(this.timeElapsed);
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
        return this.score;
    }

    public boolean isLoss() {
        return this.winPercent == 0.0;
    }

    public static final BattleResult getLoss() {
        return BattleResult.loss;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BattleResult{");
        sb.append("timeElapsed=").append(this.getTimeElapsed());
        sb.append(", winPercent=").append(this.getWinPercent());
        sb.append(", hpPercent=").append(this.getHpPercent());
        sb.append(", score=").append(this.getScore());
        sb.append(", timeElapsedVariance=").append(timeElapsedVariance);
        sb.append('}');
        return sb.toString();
    }
}
