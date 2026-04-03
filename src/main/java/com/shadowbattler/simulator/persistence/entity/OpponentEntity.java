package com.shadowbattler.simulator.persistence.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.shadowbattler.simulator.model.Opponent;
import com.shadowbattler.simulator.model.Opponent.Title;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "opponents")
public class OpponentEntity {
    @Id
    private String opponentId;
    @Enumerated(EnumType.STRING)
    private Title title;
    @Column(name = "cp_limit")
    private int limit;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "opponent_lineups", joinColumns = @JoinColumn(name = "opponent_id"))
    @Column(name = "species_id")
    private List<String> lineupSpeciesIds;
    @OneToMany(mappedBy = "opponent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BattleResultEntity> battleResults;

    public String getOpponentId() {
        return this.opponentId;
    }

    public void setOpponentId(String opponentId) {
        this.opponentId = opponentId;
    }

    public Title getTitle() {
        return this.title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public int getLimit() {
        return this.limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<String> getLineupSpeciesIds() {
        return this.lineupSpeciesIds;
    }

    public void setLineupSpeciesIds(List<String> lineupSpeciesIds) {
        this.lineupSpeciesIds = lineupSpeciesIds;
    }

    public List<BattleResultEntity> getBattleResults() {
        return this.battleResults;
    }

    public void setBattleResults(List<BattleResultEntity> battleResults) {
        this.battleResults = battleResults;
    }
    
    /**
     * @param opponent an opponent to compare to
     * @return true iff the opponents have the same id, title, limit, and lineups
     */
    public boolean representsOpponent(Opponent opponent) {
        if (!Objects.equals(this.opponentId, opponent.getOpponentId())) return false;
        if (this.title != opponent.getTitle()) return false;
        if (this.limit != opponent.getLimit()) return false;
        final Set<String> thisIds = this.lineupSpeciesIds == null ? Set.of() : new HashSet<>(this.lineupSpeciesIds);
        final Set<String> otherIds = new HashSet<>(opponent.getLineupIds().flatten());
        return thisIds.equals(otherIds);
    }

    public void updateFromOpponent(Opponent opponent) {
        this.setOpponentId(opponent.getOpponentId());
        this.setTitle(opponent.getTitle());
        this.setLimit(opponent.getLimit());
        this.setLineupSpeciesIds(opponent.getLineupIds().flatten());
    }
}
