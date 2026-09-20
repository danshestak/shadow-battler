package com.shadowbattler.simulator.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.shadowbattler.simulator.model.Creature;
import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.model.Opponent;
import com.shadowbattler.simulator.model.Species;
import com.shadowbattler.simulator.model.Stats3;
import com.shadowbattler.simulator.model.Team;
import com.shadowbattler.simulator.model.battle.BattleResult;
import com.shadowbattler.simulator.model.battle.MovesetSolver;
import com.shadowbattler.simulator.model.battle.OpponentBattleSolver;
import com.shadowbattler.simulator.persistence.entity.BattleResultEntity;
import com.shadowbattler.simulator.persistence.service.BattleResultEntityService;
import com.shadowbattler.simulator.persistence.service.MoveEntityService;
import com.shadowbattler.simulator.persistence.service.OpponentEntityService;
import com.shadowbattler.simulator.persistence.service.SpeciesEntityService;

@Service
public class BattlePersistenceService {
    private final BattleResultEntityService battleResultEntityService;
    private final SpeciesEntityService speciesEntityService;
    private final OpponentEntityService opponentEntityService;
    private final MoveEntityService moveEntityService;

    public static final List<Double> PLAYER_CREATURE_LEVELS = List.of(50.0);
    public static final List<Integer> TRAINER_LEVELS = List.of(80);

    public record BattleResultDTO(BattleResult battleResult, String speciesId, String opponentId, Move[] moveset, double playerLevel, int trainerLevel) {}

    public BattlePersistenceService(BattleResultEntityService battleResultEntityService, SpeciesEntityService speciesEntityService, OpponentEntityService opponentEntityService, MoveEntityService moveEntityService) {
        this.battleResultEntityService = battleResultEntityService;
        this.speciesEntityService = speciesEntityService;
        this.opponentEntityService = opponentEntityService;
        this.moveEntityService = moveEntityService;
    }

    public List<BattleResultDTO> createBattleResultEntities(Species species, Opponent opponent) {
        List<BattleResultDTO> resultsToSave = new ArrayList<>();
        for (int trainerLevel : TRAINER_LEVELS) {
            for (double playerCreatureLevel : PLAYER_CREATURE_LEVELS) {
                final MovesetSolver solver = new MovesetSolver(
                    (fast, charged) -> new Creature(species, Stats3.getMaxIVs(), playerCreatureLevel, fast, charged),
                    (playerCreature) -> new OpponentBattleSolver(
                        new Team<>(playerCreature, null, null), 
                        opponent,
                        trainerLevel
                    )
                );
                solver.solve();

                solver.getMovesetBattleResults().forEach(mbr -> {
                    resultsToSave.add(new BattleResultDTO(
                        mbr.getBattleResult(),
                        species.getSpeciesId(),
                        opponent.getOpponentId(),
                        mbr.getMoveset(),
                        playerCreatureLevel,
                        trainerLevel
                    ));
                });
            }
        }
        return resultsToSave;
    }

    public void persistBattles(List<BattleResultDTO> resultsToSave) {
        if (resultsToSave == null || resultsToSave.isEmpty()) {
            return;
        }
        List<BattleResultEntity> entities = resultsToSave.stream().map(dto -> {
            BattleResultEntity entity = new BattleResultEntity();
            entity.updateFromBattleResult(dto.battleResult());
            entity.setPlayerSpecies(this.speciesEntityService.getReferenceById(dto.speciesId()));
            entity.setOpponent(this.opponentEntityService.getReferenceById(dto.opponentId()));
            Move[] moveset = dto.moveset();
            if (moveset[0] != null) entity.setPlayerFastMove(this.moveEntityService.getReferenceById(moveset[0].moveId()));
            if (moveset[1] != null) entity.setPlayerChargedMove1(this.moveEntityService.getReferenceById(moveset[1].moveId()));
            if (moveset[2] != null) entity.setPlayerChargedMove2(this.moveEntityService.getReferenceById(moveset[2].moveId()));
            entity.setPlayerLevel(dto.playerLevel());
            entity.setTrainerLevel(dto.trainerLevel());
            return entity;
        }).toList();
        this.battleResultEntityService.saveAll(entities);

    }
}