package com.shadowbattler.simulator.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.model.Opponent;
import com.shadowbattler.simulator.model.Species;
import com.shadowbattler.simulator.model.Stats3;
import com.shadowbattler.simulator.model.battle.MovesetSolver;
import com.shadowbattler.simulator.persistence.entity.BattleResultEntity;
import com.shadowbattler.simulator.persistence.entity.OpponentEntity;
import com.shadowbattler.simulator.persistence.entity.SpeciesEntity;
import com.shadowbattler.simulator.persistence.service.BattleResultEntityService;
import com.shadowbattler.simulator.persistence.service.MoveEntityService;
import com.shadowbattler.simulator.persistence.service.OpponentEntityService;
import com.shadowbattler.simulator.persistence.service.SpeciesEntityService;

import jakarta.transaction.Transactional;

@Service
public class EntityReconciliationService {
    private final BattleSolverService battleSolverService;
    private final BattleResultEntityService battleResultEntityService;
    private final SpeciesEntityService speciesEntityService;
    private final SpeciesDataService speciesDataService;
    private final OpponentEntityService opponentEntityService;
    private final OpponentDataService opponentDataService;
    private final MoveEntityService moveEntityService;
    private final MovesDataService movesDataService;

    private final static List<Integer> playerCreatureLevels = List.of(50);
    private final static List<Integer> trainerLevels = List.of(80);

    public EntityReconciliationService(
        BattleSolverService battleSolverService,
        BattleResultEntityService battleResultEntityService,
        SpeciesEntityService speciesEntityService,
        SpeciesDataService speciesDataService,
        OpponentEntityService opponentEntityService,
        OpponentDataService opponentDataService,
        MoveEntityService moveEntityService,
        MovesDataService movesDataService
    ) {
        this.battleSolverService = battleSolverService;
        this.battleResultEntityService = battleResultEntityService;
        this.speciesEntityService = speciesEntityService;
        this.speciesDataService = speciesDataService;
        this.opponentEntityService = opponentEntityService;
        this.opponentDataService = opponentDataService;
        this.moveEntityService = moveEntityService;
        this.movesDataService = movesDataService;
    }

    @Transactional
    public void reconcileModified() {
        Set<Move> modifiedMoves = movesDataService.getAllMoves().stream()
            .filter(move -> {
                return this.moveEntityService.getMoveEntityById(move.moveId()).map(e -> !e.representsMove(move)).orElse(true);
            })
            .collect(Collectors.toSet());

        Set<Species> modifiedSpecies = speciesDataService.getAllSpecies().stream()
            .filter(species -> {
                final Optional<SpeciesEntity> speciesEntity = this.speciesEntityService.getSpeciesEntityById(species.getSpeciesId());

                return speciesEntity.map(e -> !e.representsSpecies(species)).orElse(true)
                    || species.getFastMoves().stream().anyMatch(modifiedMoves::contains)
                    || species.getChargedMoves().stream().anyMatch(modifiedMoves::contains);
            })
            .collect(Collectors.toSet());
        
        Set<Opponent> modifiedOpponents = opponentDataService.getAllOpponents().stream()
            .filter(opponent -> {
                final Optional<OpponentEntity> opponentEntity = this.opponentEntityService.getOpponentEntityById(opponent.getOpponentId());

                return opponentEntity.map(e -> !e.representsOpponent(opponent)).orElse(true)
                    || opponent.getLineupSpecies().stream().flatMap(Collection::stream).anyMatch(modifiedSpecies::contains);
            })
            .collect(Collectors.toSet());

        modifiedMoves.forEach(this.moveEntityService::saveMove);
        modifiedSpecies.forEach(this.speciesEntityService::saveSpecies);
        modifiedOpponents.forEach(this.opponentEntityService::saveOpponent);

        for (Opponent opponent : modifiedOpponents) {
            for (Species species : this.speciesDataService.getAllSpecies()) {
                this.reconcileBattles(species, opponent);
            }
        }

        for (Opponent opponent : this.opponentDataService.getAllOpponents()) {
            if (modifiedOpponents.contains(opponent)) continue; //avoid battles reconciled in previous loop

            for (Species species : modifiedSpecies) {
                this.reconcileBattles(species, opponent);
            }
        }

        /*
        create set of modified moves
            save changes to db entity, or create it if it doesn't exist
            remove battle results using old move

        create set of modified species (species with moveset changes, who know a modified move, or that don't exist)
            save changes to db entity, or create it if it doesn't exist
            remove battle results using old species

        create set modified opponents (opponents who use a species that has been modified)
            save changes to db entity, or create it if it doesn't exist
            remove battle results against old opponent

        rerun simulations for modified opponents
            save simulation battle results

        rerun simulations for modified species (excluding those against modified opponents as they've already happened)
            save simulation battle results

        */
    }

    public void reconcileBattles(Species species, Opponent opponent) {
        for (int trainerLevel : EntityReconciliationService.trainerLevels) {
            for (int playerCreatureLevel : EntityReconciliationService.playerCreatureLevels) {
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
                    if (moveset[0] != null) {
                        entity.setPlayerFastMove(this.moveEntityService.getReferenceById(moveset[0].moveId()));
                    }
                    if (moveset[1] != null) {
                        entity.setPlayerChargedMove1(this.moveEntityService.getReferenceById(moveset[1].moveId()));
                    }
                    if (moveset[2] != null) {
                        entity.setPlayerChargedMove2(this.moveEntityService.getReferenceById(moveset[2].moveId()));
                    }
                
                    entity.setPlayerLevel(playerCreatureLevel);
                    entity.setTrainerLevel(trainerLevel);
                
                    this.battleResultEntityService.save(entity);
                });
            }
        }
    }
}
