package com.shadowbattler.simulator.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.shadowbattler.simulator.service.MovesDataService;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Species {
    private int dex;
    private String speciesName;
    private String speciesId;
    private Stats3 baseStats;
    private Type[] types;
    @JsonProperty(value="fastMoves")
    private List<String> fastMoveIds;
    @JsonIgnore
    private List<Move> fastMoves = new ArrayList<>();
    @JsonProperty(value="chargedMoves")
    private List<String> chargedMoveIds;
    @JsonIgnore
    private List<Move> chargedMoves = new ArrayList<>();
    @JsonProperty(value="eliteMoves")
    private List<String> eliteMoveIds;
    @JsonProperty(value="legacyMoves")
    private List<String> legacyMoveIds;
    private List<Tag> tags;
    // private Map<String, List<Double>> defaultIVs;
    // private int level25CP;
    private int buddyDistance;
    private int thirdMoveCost;
    private boolean released;
    private Family family;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Family(
        String id,
        String parent,
        List<String> evolutions
    ) {}

    public enum Tag {
        SHADOW,
        SHADOWELIGIBLE,
        MEGA,
        LEGENDARY,
        MYTHICAL,
        ULTRABEAST,
        STARTER
    }

    private List<Move> hydrateMovesList(MovesDataService movesDataService, List<String> movesList) {
        if (movesList == null) return new ArrayList<>();
        return movesList.stream()
            .map(movesDataService::getMoveById)
            .toList();
    }

    public void hydrate(MovesDataService movesDataService) {
        this.fastMoves = this.hydrateMovesList(movesDataService, this.fastMoveIds);
        this.chargedMoves = this.hydrateMovesList(movesDataService, this.chargedMoveIds);
    }

    public int getDex() {
        return this.dex;
    }

    public String getSpeciesName() {
        return this.speciesName;
    }

    public String getSpeciesId() {
        return this.speciesId;
    }

    public Stats3 getBaseStats() {
        return this.baseStats;
    }

    public Type[] getTypes() {
        return this.types;
    }

    public List<Move> getFastMoves() {
        return this.fastMoves;
    }

    @JsonProperty("fastMoves")
    @SuppressWarnings("unused")
    private List<String> getFastMoveIds() {
        return this.fastMoveIds;
    }

    public List<Move> getChargedMoves() {
        return this.chargedMoves;
    }

    @JsonProperty("chargedMoves")
    @SuppressWarnings("unused")
    private List<String> getChargedMoveIds() {
        return this.chargedMoveIds;
    }

    public List<String> getEliteMoveIds() {
        return this.eliteMoveIds;
    }

    public List<String> getLegacyMoveIds() {
        return this.legacyMoveIds;
    }

    public List<Tag> getTags() {
        return this.tags;
    }

    public int getBuddyDistance() {
        return this.buddyDistance;
    }

    public int getThirdMoveCost() {
        return this.thirdMoveCost;
    }

    public boolean isReleased() {
        return this.released;
    }

    public Family getFamily() {
        return this.family;
    }
}
