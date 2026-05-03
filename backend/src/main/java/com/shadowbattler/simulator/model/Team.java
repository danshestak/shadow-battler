package com.shadowbattler.simulator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Team<T> {
    private final T first;
    private final T second;
    private final T third;

    private void validate() {
        if (this.first == null && (this.second != null || this.third != null)) {
            throw new IllegalArgumentException("teams cannot have a second or third member without a first");
        }
        if (this.second == null && this.third != null) {
            throw new IllegalArgumentException("teams cannot have a third member without a second");
        }
    }

    public Team(T first, T second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.validate();
    }

    public Team(List<T> list) {
        this.first = (list != null && !list.isEmpty()) ? list.get(0) : null;
        this.second = (list != null && list.size() > 1) ? list.get(1) : null;
        this.third = (list != null && list.size() > 2) ? list.get(2) : null;
        this.validate();
    }

    public Stream<T> stream() {
        return Stream.of(this.first, this.second, this.third);
    }

    public List<T> toList() {
        return this.stream().filter(Objects::nonNull).collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }

    public T getFirst() {
        return this.first;
    }

    public T getSecond() {
        return this.second;
    }

    public T getThird() {
        return this.third;
    }

    public T getByInt(int i) {
        return switch (i) {
            case 1 -> this.first;
            case 2 -> this.second;
            case 3 -> this.third;
            default -> throw new IllegalArgumentException(String.format("argument %n must be 1, 2, or 3", i));
        };
    }

    public int size() {
        return (this.first == null ? 0 : 1) + (this.second == null ? 0 : 1) + (this.third == null ? 0 : 1);
    }

    @Override
    public String toString() {
        return "Team"+this.toList().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team<?> team = (Team<?>) o;
        return Objects.equals(first, team.first) &&
               Objects.equals(second, team.second) &&
               Objects.equals(third, team.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }
}
