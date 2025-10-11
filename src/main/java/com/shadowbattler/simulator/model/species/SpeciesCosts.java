package com.shadowbattler.simulator.model.species;

public class SpeciesCosts {
    private final int buddyDistance;
    private final int thirdMoveCost;

    public SpeciesCosts(int buddyDistance, int thirdMoveCost) {
        this.buddyDistance = buddyDistance;
        this.thirdMoveCost = thirdMoveCost;
    }

    public int getBuddyDistance() {
        return buddyDistance;
    }

    public int getThirdMoveCost() {
        return thirdMoveCost;
    }
}
