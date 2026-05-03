package com.shadowbattler.simulator.model;

import java.util.ArrayList;
import java.util.List;

public class Lineup<T> extends Team<List<T>> {
    public Lineup(List<T> first, List<T> second, List<T> third) {
        super(first, second, third);
    }

    public Lineup() {
        super(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public int combinationQuantity() {
        return this.getFirst().size() * this.getSecond().size() * this.getThird().size();
    }

    public Team<T> combinationFromId(int combinationId) {
        if (this.getFirst().isEmpty() || this.getSecond().isEmpty() || this.getThird().isEmpty()) {
            throw new IllegalStateException("cannot get combination from id of empty lineup");
        }
        return new Team<>(
            this.getFirst().get(combinationId % this.getFirst().size()),
            this.getSecond().get((combinationId / this.getFirst().size()) % this.getSecond().size()),
            this.getThird().get((combinationId / (this.getFirst().size()*this.getSecond().size())) % this.getThird().size())
        );
    }
    
    public static <T> Lineup<T> empty() {
        return new Lineup<>(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public List<T> flatten() {
        return this.stream().flatMap((slot) -> slot.stream()).collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }
}