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

    @JsonIgnore
    private List<Move[]> cachedPlayerMoveCombinations;
    @JsonIgnore
    private List<Move[]> cachedEnemyMoveCombinations;

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
        this.thirdMoveCost = node.isInt() ? node.asInt() : -1;
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
            .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    } 

    private List<Move> hydrateMovesList(MovesDataService movesDataService, List<String> movesList) {
        if (movesList == null || movesList.size() < 1) return new ArrayList<>();
        return movesList.stream().map(movesDataService::getMoveById).collect(java.util.stream.Collectors.toCollection(ArrayList::new));
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

        final String requiredChargedMoveId = switch (this.speciesId) {
            case "rayquaza_mega" -> "DRAGON_ASCENT";
            case "zacian_crowned_sword" -> "BEHEMOTH_BLADE";
            case "zamazenta_crowned_shield" -> "BEHEMOTH_BASH";
            default -> null;
        };

        if (requiredChargedMoveId != null) {
            this.requiredChargedMove = movesDataService.getMoveById(requiredChargedMoveId);
        }

        this.fastMoves = this.hydrateMovesList(movesDataService, this.fastMoveIds);
        this.enemyFastMoves = this.hydrateMovesList(
            movesDataService, 
            this.fastMoveIds.stream().filter(
                (id) -> !this.eliteMoveIds.contains(id) && !this.legacyMoveIds.contains(id)
            ).collect(java.util.stream.Collectors.toCollection(ArrayList::new))
        );

        this.chargedMoves = new ArrayList<>(this.hydrateMovesList(movesDataService, this.chargedMoveIds));
        this.chargedMoves.sort(java.util.Comparator.comparingInt(Move::energy));

        this.enemyChargedMoves = new ArrayList<>(this.hydrateMovesList(
            movesDataService, 
            this.chargedMoveIds.stream().filter(
                (id) -> (!this.eliteMoveIds.contains(id) && !this.legacyMoveIds.contains(id)) || 
                        (this.requiredChargedMove != null && this.requiredChargedMove.moveId().equals(id))
            ).collect(java.util.stream.Collectors.toCollection(ArrayList::new))
        ));
        this.enemyChargedMoves.sort(java.util.Comparator.comparingInt(Move::energy));

    this.cachedPlayerMoveCombinations = new ArrayList<>();
    int playerQty = this.calculateMoveCombinationQuantity(false);
    for (int i = 0; i < playerQty; i++) {
        this.cachedPlayerMoveCombinations.add(this.calculateMoveCombinationFromId(i, false));
    }

    this.cachedEnemyMoveCombinations = new ArrayList<>();
    int enemyQty = this.calculateMoveCombinationQuantity(true);
    for (int i = 0; i < enemyQty; i++) {
        this.cachedEnemyMoveCombinations.add(this.calculateMoveCombinationFromId(i, true));
    }
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

    public boolean isThirdMoveEnabled() {
        return this.thirdMoveCost != -1;
    }

    /**
     * @param enemyMoves true if this counts an enemy's possible movesets. enemies don't have
     * access to elite moves, legacy moves, or a second charged move, greatly reducing their
     * quantity of move combinations 
     * @return the number of possible move combinations
     */
    public int moveCombinationQuantity(boolean enemyMoves) {
        return enemyMoves ? this.cachedEnemyMoveCombinations.size() : this.cachedPlayerMoveCombinations.size();
    }

    private int calculateMoveCombinationQuantity(boolean enemyMoves) {
        final int chargedSize = (enemyMoves ? this.enemyChargedMoves : this.chargedMoves).size();
        final int fastSize = (enemyMoves ? this.enemyFastMoves : this.fastMoves).size();

        if (enemyMoves || !this.isThirdMoveEnabled()) {
            return fastSize * (this.requiredChargedMove == null ? chargedSize : 1);
        } else {
            return fastSize * Math.max(
                this.requiredChargedMove == null ? chargedSize*(chargedSize-1)/2 : (chargedSize-1),
                1
            );
        }
    }

    /**
     * returns the array of 3 Moves that corresponds to the combinationId, with the
     * first move being the fast move, the second being the first charged move, and the
     * third being the second charged move (if applicable, null otherwise)
     * @param combinationId the id of the move combination, on the interval [0, this.moveCombinationQuantity)
     * @param enemyMoves true if this is for an enemy's possible moves. in that case,
     * the second charged move is always null, and elite and legacy moves are not included
     * @return
     */
    public Move[] moveCombinationFromId(int combinationId, boolean enemyMoves) {
        return enemyMoves ? this.cachedEnemyMoveCombinations.get(combinationId) : this.cachedPlayerMoveCombinations.get(combinationId);
    }

    private Move[] calculateMoveCombinationFromId(int combinationId, boolean enemyMoves) {
        final List<Move> fast = enemyMoves ? this.enemyFastMoves : this.fastMoves;
        final List<Move> charged = enemyMoves ? this.enemyChargedMoves : this.chargedMoves;
        final int fastSize = fast.size();
        final int chargedSize = charged.size();

        Move fastMove = fast.get(combinationId % fastSize);
        Move charged1 = null;
        Move charged2 = null;

        if (enemyMoves || !this.isThirdMoveEnabled()) {
            if (this.requiredChargedMove != null) {
                charged1 = this.requiredChargedMove;
            } else {
                charged1 = charged.get((combinationId/fastSize) % chargedSize);
            }
        } else if (!enemyMoves) {
            if (this.requiredChargedMove != null) {
                charged1 = this.requiredChargedMove;
                if (chargedSize > 1) {
                    List<Move> otherCharged = new ArrayList<>(charged);
                    otherCharged.remove(this.requiredChargedMove);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Species species = (Species) o;
        return Objects.equals(speciesId, species.speciesId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(speciesId);
    }
}
