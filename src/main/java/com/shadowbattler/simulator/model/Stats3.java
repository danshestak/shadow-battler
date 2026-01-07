package com.shadowbattler.simulator.model;

public class Stats3<T> {
    private final T atk;
    private final T def;
    private final T hp;

    private static final Stats3<Integer> maxIVs = new Stats3<>(15, 15, 15);

    public enum Stat {
        ATK,
        DEF,
        HP;
    }

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

    public static Stats3<Integer> getMaxIVs() {
        return Stats3.maxIVs;
    }

    public T getByEnum(Stats3.Stat stat) {
        return switch (stat) {
            case Stats3.Stat.ATK -> this.getAtk();
            case Stats3.Stat.DEF -> this.getDef();
            case Stats3.Stat.HP -> this.getHp();
        };
    }
}
