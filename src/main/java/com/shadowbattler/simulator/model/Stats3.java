package com.shadowbattler.simulator.model;

public class Stats3<T> {
    private final T atk;
    private final T def;
    private final T hp;

    public Stats3(T atk, T def, T hp) {
        this.atk = atk;
        this.def = def;
        this.hp = hp;
    }

    public T getAtk() {
        return atk;
    }

    public T getDef() {
        return def;
    }

    public T getHp() {
        return hp;
    }
}
