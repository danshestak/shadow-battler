package com.shadowbattler.simulator.persistence.entity;

import java.util.List;

import com.shadowbattler.simulator.model.Lineup;
import com.shadowbattler.simulator.model.Opponent.Title;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "opponents")
public class OpponentEntity {
    @Id
    private String opponentId;
    @Enumerated(EnumType.STRING)
    private Title title;
    private int limit;
    @Convert(converter = LineupStringConverter.class)
    @Column(columnDefinition = "TEXT")
    private Lineup<String> lineupIds;
    @OneToMany(mappedBy = "opponent")
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

    public Lineup<String> getLineupIds() {
        return this.lineupIds;
    }

    public void setLineupIds(Lineup<String> lineupIds) {
        this.lineupIds = lineupIds;
    }

    public List<BattleResultEntity> getBattleResults() {
        return this.battleResults;
    }

    public void setBattleResults(List<BattleResultEntity> battleResults) {
        this.battleResults = battleResults;
    }
}
