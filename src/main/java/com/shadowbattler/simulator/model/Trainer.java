package com.shadowbattler.simulator.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shadowbattler.simulator.service.SpeciesDataService;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Trainer {
    private String trainerId;
    private String name;
    private Title title;
    private int limit;
    @JsonProperty(value = "lineup")
    private Lineup<String> lineupIds;
    @JsonIgnore
    private Lineup<Species> lineupSpecies;

    public static enum Title {
        ROCKET_BOSS,
        ROCKET_LEADER,
        ROCKET_GRUNT,
        TEAM_LEADER
    }

    public static record Lineup<T> (
        List<T> first,
        List<T> second,
        List<T> third
    ) {
        public int combinationQuantity() {
            return this.first.size() * this.second.size() * this.third.size();
        }

        public Team<T> combinationFromId(int combinationId) {
            return new Team<>(
                this.first.get(combinationId % first.size()),
                this.second.get((combinationId / this.first.size()) % this.second.size()),
                this.third.get((combinationId / (this.first.size()*this.second.size())) % this.third.size())
            );
        }
    }

    private List<Species> hydrateLineupSlot(SpeciesDataService speciesDataService, List<String> idList) {
        final List<Species> hydrated = new ArrayList<>();
        if (idList == null) return hydrated;
        for (String speciesId : idList) {
            if (!speciesDataService.speciesExists(speciesId) && speciesId.endsWith("_shadow")) {
                speciesDataService.createShadowFor(speciesId.substring(0, speciesId.length() - "_shadow".length()));
            }
            hydrated.add(speciesDataService.getSpeciesById(speciesId));
        }
        return hydrated;
    }

    public void hydrate(SpeciesDataService speciesDataService) {
        this.lineupSpecies = new Lineup<>(
            hydrateLineupSlot(speciesDataService, this.lineupIds.first),
            hydrateLineupSlot(speciesDataService, this.lineupIds.second),
            hydrateLineupSlot(speciesDataService, this.lineupIds.third)
        );
    }

    public String getTrainerId() {
        return this.trainerId;
    }

    public String getName() {
        return this.name;
    }

    public Title getTitle() {
        return this.title;
    }

    public int getLimit() {
        return this.limit;
    }

    public Lineup<String> getLineupIds() {
        return this.lineupIds;
    }

    public Lineup<Species> getLineupSpecies() {
        return this.lineupSpecies;
    }
}
