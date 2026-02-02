package com.shadowbattler.simulator.model;

import java.util.Objects;

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

    @Override
    public String toString() {
        return new StringBuilder()
            .append("Stats3[")
            .append(this.atk)
            .append(", ")
            .append(this.def)
            .append(", ")
            .append(this.hp)
            .append("]")
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Stats3<?> stats3 = (Stats3<?>) o;
        return Objects.equals(atk, stats3.atk) && Objects.equals(def, stats3.def) && Objects.equals(hp, stats3.hp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(atk, def, hp);
    }
}
