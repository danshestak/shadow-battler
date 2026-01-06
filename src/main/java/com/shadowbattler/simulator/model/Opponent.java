package com.shadowbattler.simulator.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shadowbattler.simulator.service.SpeciesDataService;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Opponent {
    private String opponentId;
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

    public static class Lineup<T> extends Team<List<T>> {
        private Lineup(List<T> first, List<T> second, List<T> third) {
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
            hydrateLineupSlot(speciesDataService, this.lineupIds.getFirst()),
            hydrateLineupSlot(speciesDataService, this.lineupIds.getSecond()),
            hydrateLineupSlot(speciesDataService, this.lineupIds.getThird())
        );
    }

    public String getOpponentId() {
        return this.opponentId;
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
