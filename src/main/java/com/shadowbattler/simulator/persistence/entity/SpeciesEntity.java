// package com.shadowbattler.simulator.persistence.entity;

// import java.util.List;

// import jakarta.persistence.Column;
// import jakarta.persistence.Convert;
// import jakarta.persistence.Entity;
// import jakarta.persistence.Id;
// import jakarta.persistence.OneToMany;
// import jakarta.persistence.Table;

// @Entity
// @Table(name = "species")
// public class SpeciesEntity {
//     @Id
//     private String speciesId;
//     @Convert(converter = StringListConverter.class)
//     @Column(columnDefinition = "TEXT")
//     private List<String> fastMoveIds;
//     @Convert(converter = StringListConverter.class)
//     @Column(columnDefinition = "TEXT")
//     private List<String> chargedMoveIds;
//     @Convert(converter = StringListConverter.class)
//     @Column(columnDefinition = "TEXT")
//     private List<String> eliteMoveIds;
//     @Convert(converter = StringListConverter.class)
//     @Column(columnDefinition = "TEXT")
//     private List<String> legacyMoveIds;
//     @OneToMany(mappedBy = "playerSpecies")
//     private List<BattleResultEntity> battleResults;

//     public String getSpeciesId() {
//         return this.speciesId;
//     }

//     public void setSpeciesId(String speciesId) {
//         this.speciesId = speciesId;
//     }

//     public List<String> getFastMoveIds() {
//         return this.fastMoveIds;
//     }

//     public void setFastMoveIds(List<String> fastMoveIds) {
//         this.fastMoveIds = fastMoveIds;
//     }

//     public List<String> getChargedMoveIds() {
//         return this.chargedMoveIds;
//     }

//     public void setChargedMoveIds(List<String> chargedMoveIds) {
//         this.chargedMoveIds = chargedMoveIds;
//     }

//     public List<String> getEliteMoveIds() {
//         return this.eliteMoveIds;
//     }

//     public void setEliteMoveIds(List<String> eliteMoveIds) {
//         this.eliteMoveIds = eliteMoveIds;
//     }

//     public List<String> getLegacyMoveIds() {
//         return this.legacyMoveIds;
//     }

//     public void setLegacyMoveIds(List<String> legacyMoveIds) {
//         this.legacyMoveIds = legacyMoveIds;
//     }

//     public List<BattleResultEntity> getBattleResults() {
//         return this.battleResults;
//     }

//     public void setBattleResults(List<BattleResultEntity> battleResults) {
//         this.battleResults = battleResults;
//     }
// }
