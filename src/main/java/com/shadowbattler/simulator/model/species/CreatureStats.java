package com.shadowbattler.simulator.model.species;

public class CreatureStats {
    private int level;
    private Stats3 ivs;

    public CreatureStats(Stats3 ivs, int level) {
        this.ivs = ivs;
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public Stats3 getIvs() {
        return ivs;
    }
}
