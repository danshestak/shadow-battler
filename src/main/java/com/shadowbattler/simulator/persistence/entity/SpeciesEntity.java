package com.shadowbattler.simulator.persistence.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.shadowbattler.simulator.model.Species;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/*
the only things that ever change about a species relevant to simulations are the movesets, so that is the only data stored. legacy
and elite move data is also necessary to keep track of as it dictates what movesets opponents can have (since they can't have either
elite or legacy moves)
*/
@Entity
@Table(name = "species")
public class SpeciesEntity {
    @Id
    private String speciesId;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "species_fast_moves", joinColumns = @JoinColumn(name = "species_id"))
    @Column(name = "move_id")
    private Set<String> fastMoveIds;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "species_charged_moves", joinColumns = @JoinColumn(name = "species_id"))
    @Column(name = "move_id")
    private Set<String> chargedMoveIds;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "species_elite_moves", joinColumns = @JoinColumn(name = "species_id"))
    @Column(name = "move_id")
    private Set<String> eliteMoveIds;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "species_legacy_moves", joinColumns = @JoinColumn(name = "species_id"))
    @Column(name = "move_id")
    private Set<String> legacyMoveIds;
    @OneToMany(mappedBy = "playerSpecies", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BattleResultEntity> battleResults;

    public String getSpeciesId() {
        return this.speciesId;
    }

    public void setSpeciesId(String speciesId) {
        this.speciesId = speciesId;
    }

    public Set<String> getFastMoveIds() {
        return this.fastMoveIds;
    }

    public void setFastMoveIds(Set<String> fastMoveIds) {
        this.fastMoveIds = fastMoveIds;
    }

    public Set<String> getChargedMoveIds() {
        return this.chargedMoveIds;
    }

    public void setChargedMoveIds(Set<String> chargedMoveIds) {
        this.chargedMoveIds = chargedMoveIds;
    }

    public Set<String> getEliteMoveIds() {
        return this.eliteMoveIds;
    }

    public void setEliteMoveIds(Set<String> eliteMoveIds) {
        this.eliteMoveIds = eliteMoveIds;
    }

    public Set<String> getLegacyMoveIds() {
        return this.legacyMoveIds;
    }

    public void setLegacyMoveIds(Set<String> legacyMoveIds) {
        this.legacyMoveIds = legacyMoveIds;
    }

    public List<BattleResultEntity> getBattleResults() {
        return this.battleResults;
    }

    public void setBattleResults(List<BattleResultEntity> battleResults) {
        this.battleResults = battleResults;
    }

    private static Set<String> listAsSet(List<String> list) {
        return list == null ? Set.of() : new HashSet<>(list);
    }

    private static boolean setEqualsList(Set<String> set, List<String> list) {
        return (set == null ? Set.of() : set).equals(SpeciesEntity.listAsSet(list));
    }

    /**
     * @param species a species to compare to
     * @return true iff all of this entity's move ids match the move ids of the species being compared to
     */
    public boolean representsSpecies(Species species) {
        if (!SpeciesEntity.setEqualsList(this.fastMoveIds, species.getFastMoveIds())) return false;
        if (!SpeciesEntity.setEqualsList(this.chargedMoveIds, species.getChargedMoveIds())) return false;
        if (!SpeciesEntity.setEqualsList(this.eliteMoveIds, species.getEliteMoveIds())) return false;
        return SpeciesEntity.setEqualsList(this.legacyMoveIds, species.getLegacyMoveIds());
    }
    
    public void updateFromSpecies(Species species) {
        this.setSpeciesId(species.getSpeciesId());
        this.setFastMoveIds(SpeciesEntity.listAsSet(species.getFastMoveIds()));
        this.setChargedMoveIds(SpeciesEntity.listAsSet(species.getChargedMoveIds()));
        this.setEliteMoveIds(SpeciesEntity.listAsSet(species.getEliteMoveIds()));
        this.setLegacyMoveIds(SpeciesEntity.listAsSet(species.getLegacyMoveIds()));
    }
}
