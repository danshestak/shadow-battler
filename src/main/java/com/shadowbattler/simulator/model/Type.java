package com.shadowbattler.simulator.model;

public enum Type {
    NORMAL(0),
    FIRE(1),
    WATER(2),
    GRASS(3),
    ELECTRIC(4),
    ICE(5),
    FIGHTING(6),
    POISON(7),
    GROUND(8),
    FLYING(9),
    PSYCHIC(10),
    BUG(11),
    ROCK(12),
    GHOST(13),
    DRAGON(14),
    DARK(15),
    STEEL(16),
    FAIRY(17),
    NONE(-1);

    private static final int[][] MATCHUPS = {
        {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, -1, -2,  0,  0, -1,  0  }, // Normal
        {  0, -1, -1,  1,  0,  1,  0,  0,  0,  0,  0,  1, -1,  0, -1,  0,  1,  0  }, // Fire
        {  0,  1, -1, -1,  0,  0,  0,  0,  1,  0,  0,  0,  1,  0, -1,  0,  0,  0  }, // Water
        {  0, -1,  1, -1,  0,  0,  0, -1,  1, -1,  0, -1,  1,  0, -1,  0, -1,  0  }, // Grass
        {  0,  0,  1, -1, -1,  0,  0,  0, -2,  1,  0,  0,  0,  0, -1,  0,  0,  0  }, // Electric
        {  0, -1, -1,  1,  0, -1,  0,  0,  1,  1,  0,  0,  0,  0,  1,  0, -1,  0  }, // Ice
        {  1,  0,  0,  0,  0,  1,  0, -1,  0, -1, -1, -1,  1, -2,  0,  1,  1, -1  }, // Fighting
        {  0,  0,  0,  1,  0,  0,  0, -1, -1,  0,  0,  0, -1, -1,  0,  0, -2,  1  }, // Poison
        {  0,  1,  0, -1,  1,  0,  0,  1,  0, -2,  0, -1,  1,  0,  0,  0,  1,  0  }, // Ground
        {  0,  0,  0,  1, -1,  0,  1,  0,  0,  0,  0,  1, -1,  0,  0,  0, -1,  0  }, // Flying
        {  0,  0,  0,  0,  0,  0,  1,  1,  0,  0, -1,  0,  0,  0,  0, -2, -1,  0  }, // Psychic
        {  0, -1,  0,  1,  0,  0, -1, -1,  0, -1,  1,  0,  0, -1,  0,  1, -1, -1  }, // Bug
        {  0,  1,  0,  0,  0,  1, -1,  0, -1,  1,  0,  1,  0,  0,  0,  0, -1,  0  }, // Rock
        { -2,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  0,  0,  1,  0, -1,  0,  0  }, // Ghost
        {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  1,  0, -1, -2  }, // Dragon
        {  0,  0,  0,  0,  0,  0, -1,  0,  0,  0,  1,  0,  0,  1,  0, -1,  0, -1  }, // Dark
        {  0, -1, -1,  0, -1,  1,  0,  0,  0,  0,  0,  0,  1,  0,  0,  0, -1,  1  }, // Steel
        {  0, -1,  0,  0,  0,  0,  1, -1,  0,  0,  0,  0,  0,  0,  1,  1, -1,  0  }  // Fairy
    };

    private static final double[][] EFFECTIVENESS = new double[18][18];

    static {
        for (int i = 0; i < 18; i++) {
            for (int j = 0; j < 18; j++) {
                EFFECTIVENESS[i][j] = Math.pow(1.6, Type.MATCHUPS[i][j]);
            }
        }
    }

    private final int id;

    private Type(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public double effectivenessAgainst(Type defender) {
        if (defender == Type.NONE) return 1;
        return Type.EFFECTIVENESS[this.getId()][defender.getId()];
    }

    public double effectivenessAgainst(Type[] defendingTypes) {
        double product = 1;
        for (Type defendingType : defendingTypes) {
            product *= this.effectivenessAgainst(defendingType);
        }
        return product;
    }
}
