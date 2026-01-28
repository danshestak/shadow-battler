package com.shadowbattler.simulator.model;

import java.util.List;

public class Lineup<T> extends Team<List<T>> {
    public Lineup(List<T> first, List<T> second, List<T> third) {
        super(first, second, third);
    }

    public int combinationQuantity() {
        return this.getFirst().size() * this.getSecond().size() * this.getThird().size();
    }

    public Team<T> combinationFromId(int combinationId) {
        return new Team<>(
            this.getFirst().get(combinationId % this.getFirst().size()),
            this.getSecond().get((combinationId / this.getFirst().size()) % this.getSecond().size()),
            this.getThird().get((combinationId / (this.getFirst().size()*this.getSecond().size())) % this.getThird().size())
        );
    }
}