package com.shadowbattler.simulator.persistence.entity;

import java.util.List;
import java.util.Objects;

import com.shadowbattler.simulator.model.Species;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
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
    @ElementCollection
    @CollectionTable(name = "species_fast_moves", joinColumns = @JoinColumn(name = "species_id"))
    @Column(name = "move_id")
    private List<String> fastMoveIds;
    @ElementCollection
    @CollectionTable(name = "species_charged_moves", joinColumns = @JoinColumn(name = "species_id"))
    @Column(name = "move_id")
    private List<String> chargedMoveIds;
    @ElementCollection
    @CollectionTable(name = "species_elite_moves", joinColumns = @JoinColumn(name = "species_id"))
    @Column(name = "move_id")
    private List<String> eliteMoveIds;
    @ElementCollection
    @CollectionTable(name = "species_legacy_moves", joinColumns = @JoinColumn(name = "species_id"))
    @Column(name = "move_id")
    private List<String> legacyMoveIds;
    @OneToMany(mappedBy = "playerSpecies", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BattleResultEntity> battleResults;

    public String getSpeciesId() {
        return this.speciesId;
    }

    public void setSpeciesId(String speciesId) {
        this.speciesId = speciesId;
    }

    public List<String> getFastMoveIds() {
        return this.fastMoveIds;
    }

    public void setFastMoveIds(List<String> fastMoveIds) {
        this.fastMoveIds = fastMoveIds;
    }

    public List<String> getChargedMoveIds() {
        return this.chargedMoveIds;
    }

    public void setChargedMoveIds(List<String> chargedMoveIds) {
        this.chargedMoveIds = chargedMoveIds;
    }

    public List<String> getEliteMoveIds() {
        return this.eliteMoveIds;
    }

    public void setEliteMoveIds(List<String> eliteMoveIds) {
        this.eliteMoveIds = eliteMoveIds;
    }

    public List<String> getLegacyMoveIds() {
        return this.legacyMoveIds;
    }

    public void setLegacyMoveIds(List<String> legacyMoveIds) {
        this.legacyMoveIds = legacyMoveIds;
    }

    public List<BattleResultEntity> getBattleResults() {
        return this.battleResults;
    }

    public void setBattleResults(List<BattleResultEntity> battleResults) {
        this.battleResults = battleResults;
    }

    /**
     * @param species a species to compare to
     * @return true iff all of this entity's move ids match the move ids of the species being compared to
     */
    public boolean representsSpecies(Species species) {
        if (!Objects.equals(this.fastMoveIds, species.getFastMoveIds())) return false;
        if (!Objects.equals(this.chargedMoveIds, species.getChargedMoveIds())) return false;
        if (!Objects.equals(this.eliteMoveIds, species.getEliteMoveIds())) return false;
        return Objects.equals(this.legacyMoveIds, species.getLegacyMoveIds());
    }
    
    public void updateFromSpecies(Species species) {
        this.setSpeciesId(species.getSpeciesId());
        this.setFastMoveIds(species.getFastMoveIds());
        this.setChargedMoveIds(species.getChargedMoveIds());
        this.setEliteMoveIds(species.getEliteMoveIds());
        this.setLegacyMoveIds(species.getLegacyMoveIds());
    }
}
