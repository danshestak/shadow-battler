package com.shadowbattler.simulator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import com.shadowbattler.simulator.service.MovesDataService;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Species {
    private int dex;
    private String speciesName;
    private String speciesId;
    private Stats3<Integer> baseStats;
    private Type[] types;
    @JsonProperty(value = "fastMoves")
    private List<String> fastMoveIds = new ArrayList<>();
    @JsonIgnore
    private List<Move> fastMoves = new ArrayList<>();
    @JsonIgnore
    private List<Move> enemyFastMoves = new ArrayList<>();
    @JsonProperty(value = "chargedMoves")
    private List<String> chargedMoveIds;
    @JsonIgnore
    private List<Move> chargedMoves = new ArrayList<>();
    @JsonIgnore
    private List<Move> enemyChargedMoves = new ArrayList<>();
    @JsonProperty(value = "eliteMoves")
    private List<String> eliteMoveIds = new ArrayList<>();
    @JsonProperty(value = "legacyMoves")
    private List<String> legacyMoveIds = new ArrayList<>();
    private Move requiredChargedMove = null;
    private List<Tag> tags = new ArrayList<>();
    // private Map<String, List<Double>> defaultIVs;
    // private int level25CP;
    private int buddyDistance;
    private int thirdMoveCost;
    private boolean released;
    private Family family;
    private boolean shadow = false;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static record Family(
        String id,
        String parent,
        List<String> evolutions
    ) {}

    public static enum Tag {
        SHADOW,
        SHADOWELIGIBLE,
        MEGA,
        LEGENDARY,
        MYTHICAL,
        ULTRABEAST,
    } 

    //used by jackson for deserialization
    @SuppressWarnings("unused")
    private Species() {}

    @JsonIgnore
    public Species(
        int dex, 
        String speciesName, 
        String speciesId, 
        Stats3<Integer> baseStats,
        Type[] types,
        List<String> fastMoveIds,
        List<String> chargedMoveIds,
        List<String> eliteMoveIds,
        List<String> legacyMoveIds,
        List<Tag> tags,
        int buddyDistance,
        int thirdMoveCost,
        boolean released,
        Family family
    ) {
        this.dex = dex;
        this.speciesName = speciesName;
        this.speciesId = speciesId;
        this.baseStats = baseStats;
        this.types = types;
        this.fastMoveIds = fastMoveIds;
        this.chargedMoveIds = chargedMoveIds;
        this.eliteMoveIds = eliteMoveIds;
        this.legacyMoveIds = legacyMoveIds;
        this.tags = tags;
        this.buddyDistance = buddyDistance;
        this.thirdMoveCost = thirdMoveCost;
        this.released = released;
        this.family = family;
    }

    @SuppressWarnings("unused")
    @JsonSetter("thirdMoveCost")
    private void setThirdMoveCost(JsonNode node) {
        this.thirdMoveCost = node.isInt() ? node.asInt() : 0;
    }

    @SuppressWarnings("unused")
    @JsonSetter("tags")
    private void setTags(List<String> stringTags) {
        if (stringTags == null) {
            this.tags = new ArrayList<>();
            return;
        }
        this.tags = stringTags.stream()
            .map((String str) -> {
                try {
                    return Tag.valueOf(str.strip().toUpperCase());
                } catch (Exception e) {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .toList();
    } 

    private List<Move> hydrateMovesList(MovesDataService movesDataService, List<String> movesList) {
        if (movesList == null || movesList.size() < 1) return new ArrayList<>();
        return movesList.stream().map(movesDataService::getMoveById).toList();
    }

    public void hydrate(MovesDataService movesDataService) {
        this.shadow = this.tags.contains(Species.Tag.SHADOW);
        if (this.shadow) {
            this.chargedMoveIds.add("FRUSTRATION");
            this.legacyMoveIds.add("FRUSTRATION");
        } else if (this.tags.contains(Species.Tag.SHADOWELIGIBLE)) {
            this.chargedMoveIds.add("RETURN");
            this.legacyMoveIds.add("RETURN");
        } else if (this.chargedMoveIds.isEmpty()) {
            this.chargedMoveIds.add("STRUGGLE");
        }

        if (this.speciesId.equals("zacian_crowned_sword")) {
            this.requiredChargedMove = movesDataService.getMoveById("BEHEMOTH_BLADE");
        } else if (this.speciesId.equals("zamazenta_crowned_shield")) {
            this.requiredChargedMove = movesDataService.getMoveById("BEHEMOTH_BASH");
        }

        this.fastMoves = this.hydrateMovesList(movesDataService, this.fastMoveIds);
        this.enemyFastMoves = this.hydrateMovesList(
            movesDataService, 
            this.fastMoveIds.stream().filter(
                (id) -> !this.eliteMoveIds.contains(id) && !this.legacyMoveIds.contains(id)
            ).toList()
        );
        this.chargedMoves = this.hydrateMovesList(movesDataService, this.chargedMoveIds);
        this.enemyChargedMoves = this.hydrateMovesList(
            movesDataService, 
            this.chargedMoveIds.stream().filter(
                (id) -> (!this.eliteMoveIds.contains(id) && !this.legacyMoveIds.contains(id)) || 
                        (this.requiredChargedMove != null && this.requiredChargedMove.moveId().equals(id))
            ).toList()
        );
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

    public Stats3<Integer> getBaseStats() {
        return this.baseStats;
    }

    public Type[] getTypes() {
        return this.types;
    }

    public List<Move> getFastMoves() {
        return this.fastMoves;
    }

    @JsonProperty("fastMoves")
    public List<String> getFastMoveIds() {
        return this.fastMoveIds;
    }

    public List<Move> getChargedMoves() {
        return this.chargedMoves;
    }

    @JsonProperty("chargedMoves")
    public List<String> getChargedMoveIds() {
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

    public boolean isShadow() {
        return this.shadow;
    }

    public boolean givesStabTo(Move move) {
        if (move == null) return false;
        for (Type type : this.types) {
            if (type == move.type()) return true;
        }
        return false;
    }

    /**
     * @param enemyMoves true if elite and legacy moves should be excluded
     * @return the number of possible move combinations
     */
    public int moveCombinationQuantity(boolean enemyMoves) {
        final int chargedSize = (enemyMoves ? this.enemyChargedMoves : this.chargedMoves).size();
        final int fastSize = (enemyMoves ? this.enemyFastMoves : this.fastMoves).size();

        return fastSize * Math.max(
            this.requiredChargedMove == null ? chargedSize*(chargedSize-1)/2 : (chargedSize-1),
            1
        );
    }

    /**
     * returns the array of 3 Moves that corresponds to the combinationId, with the
     * first move being the fast move, the second being the first charged move, and the
     * third being the second charged move (if applicable, null otherwise)
     * @param combinationId
     * @param enemyMoves
     * @return
     */
    public Move[] moveCombinationFromId(int combinationId, boolean enemyMoves) {
        final List<Move> fast = enemyMoves ? this.enemyFastMoves : this.fastMoves;
        final List<Move> charged = enemyMoves ? this.enemyChargedMoves : this.chargedMoves;
        final int fastSize = fast.size();
        final int chargedSize = charged.size();

        Move fastMove = fast.get(combinationId % fastSize);
        Move charged1 = null;
        Move charged2 = null;

        if (this.requiredChargedMove != null) {
            charged1 = this.requiredChargedMove;
            if (chargedSize > 1) {
                List<Move> otherCharged = new ArrayList<>(charged);
                otherCharged.remove(requiredChargedMove);
                int charged2Id = (combinationId / fastSize) % otherCharged.size();
                charged2 = otherCharged.get(charged2Id);
            }
        } else {
            if (chargedSize == 1) {
                charged1 = charged.get(0);
            } else if (chargedSize >= 2) {
                int chargedPairId = combinationId / fastSize;
                
                int i = 0;
                while (chargedPairId >= (chargedSize - 1 - i)) {
                    chargedPairId -= (chargedSize - 1 - i);
                    i++;
                }
                int j = i + 1 + chargedPairId;

                charged1 = charged.get(i);
                charged2 = charged.get(j);
            }
        }

        return new Move[]{
            fastMove,
            charged1,
            charged2
        };
    }

    @Override
    public String toString() {
        return this.speciesId;
    }
}
