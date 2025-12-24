package com.shadowbattler.simulator.model;

public class Stats3 {
    private final int atk;
    private final int def;
    private final int hp;

    public Stats3(int atk, int def, int hp) {
        this.atk = atk;
        this.def = def;
        this.hp = hp;
    }

    public int getAtk() {
        return atk;
    }

    public int getDef() {
        return def;
    }

    public int getHp() {
        return hp;
    }
}
