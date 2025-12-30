package com.shadowbattler.simulator.model;

import java.util.List;

public class Creature {
    private final Species species;
    private final Stats3<Integer> ivs;
    private final Stats3<Double> stats;
    private final int cp;
    private final double level;
    private final Move fastMove;
    private final List<Move> chargedMoves;

    private static final double[] cpMultipliers = {
        0.094, //1.0
        0.13513743, //1.5
        0.16639787, //2.0
        0.19265091, //...
        0.21573247,
        0.23657266,
        0.25572005,
        0.27353038,
        0.29024988,
        0.30605738,
        0.3210876,
        0.33544503,
        0.34921268,
        0.36245776,
        0.3752356,
        0.38759242,
        0.39956728,
        0.41119354,
        0.4225,
        0.43292641,
        0.44310755,
        0.45305996,
        0.4627984,
        0.47233608,
        0.48168495,
        0.49085581,
        0.49985844,
        0.50870176,
        0.51739395,
        0.5259425,
        0.5343543,
        0.54263575,
        0.5507927,
        0.55883059,
        0.5667545,
        0.57456913,
        0.5822789,
        0.5898879,
        0.5974,
        0.60482366,
        0.6121573,
        0.61940411,
        0.6265671,
        0.63364917,
        0.64065295,
        0.64758096,
        0.65443563,
        0.66121926,
        0.667934,
        0.67458189,
        0.6811649,
        0.68768489,
        0.69414365,
        0.70054289,
        0.7068842,
        0.7131691,
        0.7193991,
        0.72557562,
        0.7317,
        0.73474102,
        0.7377695,
        0.74078558,
        0.74378943,
        0.7467812,
        0.74976104,
        0.7527291,
        0.7556855,
        0.75863036,
        0.76156384,
        0.76448607,
        0.76739717,
        0.77029727,
        0.7731865,
        0.77606494,
        0.77893275,
        0.78179008,
        0.784637,
        0.78747359,
        0.7903,
        0.79280394,
        0.7953,
        0.79780392,
        0.8003,
        0.80280389,
        0.8053,
        0.80780387,
        0.8103,
        0.81280384,
        0.8153,
        0.81780382,
        0.8203,
        0.8228038,
        0.8253,
        0.82780378,
        0.8303,
        0.83280375,
        0.8353,
        0.83780373,
        0.8403,
        0.84280371,
        0.8453,
        0.84780369,
        0.8503,
        0.85280366,
        0.8553,
        0.85780364,
        0.8603, //...
        0.86280362, //54.5
        0.8653 //55.0
    };

    private static double getCpMultiplierAtLevel(double level) {
        if (level < 1.0 || level > 55.0) {
            throw new IllegalArgumentException(String.format(
                "expected level to be between 1.0 and 55.0, received %f", level
            ));
        }
        return Creature.cpMultipliers[(int)Math.round((level-1.0)*2)];
    }

    public Creature(Species species, Stats3<Integer> ivs, double level, Move fastMove, List<Move> chargedMoves) {
        this.species = species;
        this.ivs = ivs;
        this.level = level;
        this.fastMove = fastMove;
        this.chargedMoves = chargedMoves;

        final double cpMultiplier = Creature.getCpMultiplierAtLevel(level);
        final double atk = (species.getBaseStats().getAtk() + ivs.getAtk())*cpMultiplier;
        final double def = (species.getBaseStats().getDef() + ivs.getDef())*cpMultiplier;
        final double hp = (species.getBaseStats().getHp() + ivs.getHp())*cpMultiplier;

        this.stats = new Stats3<>(atk, def, Math.max(10.0, Math.floor(hp)));

        this.cp = Math.max(
            (int)Math.floor((atk * Math.sqrt(def) * Math.sqrt(hp))/10), 
            10
        );
    }

    public Species getSpecies() {
        return species;
    }

    public Stats3<Integer> getIvs() {
        return this.ivs;
    }

    public Stats3<Double> getStats() {
        return this.stats;
    }

    public int getCp() {
        return this.cp;
    }

    public double getLevel() {
        return this.level;
    }

    public Move getFastMove() {
        return this.fastMove;
    }

    public List<Move> getChargedMoves() {
        return this.chargedMoves;
    }
}
