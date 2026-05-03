package com.shadowbattler.simulator.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.model.Opponent;
import com.shadowbattler.simulator.model.Species;
import com.shadowbattler.simulator.model.Stats3;
import com.shadowbattler.simulator.model.battle.MovesetSolver;
import com.shadowbattler.simulator.persistence.entity.BattleResultEntity;
import com.shadowbattler.simulator.persistence.service.BattleResultEntityService;
import com.shadowbattler.simulator.persistence.service.MoveEntityService;
import com.shadowbattler.simulator.persistence.service.OpponentEntityService;
import com.shadowbattler.simulator.persistence.service.SpeciesEntityService;

@Service
public class BattlePersistenceService {
    private final BattleSolverService battleSolverService;
    private final BattleResultEntityService battleResultEntityService;
    private final SpeciesEntityService speciesEntityService;
    private final OpponentEntityService opponentEntityService;
    private final MoveEntityService moveEntityService;

    private static final List<Integer> PLAYER_CREATURE_LEVELS = List.of(50);
    private static final List<Integer> TRAINER_LEVELS = List.of(80);

    public BattlePersistenceService(BattleSolverService battleSolverService, BattleResultEntityService battleResultEntityService, SpeciesEntityService speciesEntityService, OpponentEntityService opponentEntityService, MoveEntityService moveEntityService) {
        this.battleSolverService = battleSolverService;
        this.battleResultEntityService = battleResultEntityService;
        this.speciesEntityService = speciesEntityService;
        this.opponentEntityService = opponentEntityService;
        this.moveEntityService = moveEntityService;
    }

    public List<BattleResultEntity> createBattleResultEntities(Species species, Opponent opponent) {
        List<BattleResultEntity> resultsToSave = new ArrayList<>();
        for (int trainerLevel : TRAINER_LEVELS) {
            for (int playerCreatureLevel : PLAYER_CREATURE_LEVELS) {
                final MovesetSolver solution = this.battleSolverService.solveOpponentMovesetSolver(
                    species,
                    Stats3.getMaxIVs(),
                    playerCreatureLevel,
                    opponent,
                    trainerLevel
                );

                solution.getMovesetBattleResults().forEach(mbr -> {
                    BattleResultEntity entity = new BattleResultEntity();
                    entity.updateFromBattleResult(mbr.getBattleResult());
                    entity.setPlayerSpecies(this.speciesEntityService.getReferenceById(species.getSpeciesId()));
                    entity.setOpponent(this.opponentEntityService.getReferenceById(opponent.getOpponentId()));
                    Move[] moveset = mbr.getMoveset();
                    if (moveset[0] != null) entity.setPlayerFastMove(this.moveEntityService.getReferenceById(moveset[0].moveId()));
                    if (moveset[1] != null) entity.setPlayerChargedMove1(this.moveEntityService.getReferenceById(moveset[1].moveId()));
                    if (moveset[2] != null) entity.setPlayerChargedMove2(this.moveEntityService.getReferenceById(moveset[2].moveId()));
                    entity.setPlayerLevel(playerCreatureLevel);
                    entity.setTrainerLevel(trainerLevel);
                    resultsToSave.add(entity);
                });
            }
        }
        return resultsToSave;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void persistBattles(List<BattleResultEntity> resultsToSave) {
        if (resultsToSave == null || resultsToSave.isEmpty()) {
            return;
        }
        this.battleResultEntityService.saveAll(resultsToSave);
    }
}