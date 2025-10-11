package com.shadowbattler.simulator.model.species;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

//data from pvpoke that is not from pokemon go itself
public class SpeciesMetadata {
    private static final Set<Integer> ALLOWED_DEFAULTSTATS_KEYS = Set.of(500, 1500, 2500);

    private final String[] tags;
    private final boolean released;
    private final int level25CP;
    private final Map<Integer, CreatureStats> defaultStats;

    public SpeciesMetadata(Map<Integer, CreatureStats> defaultStats, int level25CP, boolean released, String[] tags) {
        for (Integer defaultStatsKey : defaultStats.keySet()) {
            if (!ALLOWED_DEFAULTSTATS_KEYS.contains(defaultStatsKey)) {
                throw new IllegalArgumentException(
                    String.format("defaultStats key %d is not of set: %s", defaultStatsKey, ALLOWED_DEFAULTSTATS_KEYS)
                );
            }
        }

        this.defaultStats = defaultStats;
        this.level25CP = level25CP;
        this.released = released;
        this.tags = Arrays.copyOf(tags, tags.length);
    }

    public String[] getTags() {
        return Arrays.copyOf(tags, tags.length);
    }

    public boolean isReleased() {
        return released;
    }

    public int getLevel25CP() {
        return level25CP;
    }

    public CreatureStats getDefaultStats(int cp) {
        return defaultStats.get(cp);
    }
}
