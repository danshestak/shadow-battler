package com.shadowbattler.simulator.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.model.Opponent;
import com.shadowbattler.simulator.model.Species;
import com.shadowbattler.simulator.model.battle.BattleResult;
import com.shadowbattler.simulator.persistence.entity.MoveEntity;
import com.shadowbattler.simulator.persistence.entity.OpponentEntity;
import com.shadowbattler.simulator.persistence.entity.SpeciesEntity;
import com.shadowbattler.simulator.persistence.service.BattleResultEntityService;
import com.shadowbattler.simulator.persistence.service.MoveEntityService;
import com.shadowbattler.simulator.persistence.service.OpponentEntityService;
import com.shadowbattler.simulator.persistence.service.SpeciesEntityService;

@Service
public class EntityReconciliationService {
    private final SpeciesEntityService speciesEntityService;
    private final SpeciesDataService speciesDataService;
    private final OpponentEntityService opponentEntityService;
    private final OpponentDataService opponentDataService;
    private final MoveEntityService moveEntityService;
    private final MovesDataService movesDataService;
    private final BattlePersistenceService battleReconciliationService;
    private final BattleResultEntityService battleResultEntityService;
    @Value("${RECONCILE_ENTITIES:true}")
    private boolean reconcileEntities;

    public EntityReconciliationService(
        SpeciesEntityService speciesEntityService,
        SpeciesDataService speciesDataService,
        OpponentEntityService opponentEntityService,
        OpponentDataService opponentDataService,
        MoveEntityService moveEntityService,
        MovesDataService movesDataService,
        BattlePersistenceService battleReconciliationService,
        BattleResultEntityService battleResultEntityService
    ) {
        this.speciesEntityService = speciesEntityService;
        this.speciesDataService = speciesDataService;
        this.opponentEntityService = opponentEntityService;
        this.opponentDataService = opponentDataService;
        this.moveEntityService = moveEntityService;
        this.movesDataService = movesDataService;
        this.battleReconciliationService = battleReconciliationService;
        this.battleResultEntityService = battleResultEntityService;
    }

    private static final List<String> speciesBlacklist = List.of("smeargle", "morpeko", "aegislash", "mimikyu");

    public static boolean speciesFilter(Species species) {
        if (species.getFamily() != null && species.getFamily().evolutions() != null) return false;
        return !speciesBlacklist.stream().anyMatch(species.getSpeciesId()::startsWith);
    }

    @Async
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void reconcileModified() {
        final List<Species> allSpecies = speciesDataService.getAllSpecies();
        final List<Opponent> allOpponents = opponentDataService.getAllOpponents();

        final List<Species> filteredSpecies = allSpecies.stream()
            .filter(EntityReconciliationService::speciesFilter)
            .toList();
            
        final Map<String, MoveEntity> existingMoves = this.moveEntityService.getAllMoveEntities().stream()
            .collect(Collectors.toMap(m -> m.getMoveId(), Function.identity()));

        Set<Move> modifiedMoves = movesDataService.getAllMoves().stream()
            .filter(move -> {
                MoveEntity e = existingMoves.get(move.moveId());
                return e == null || !e.representsMove(move);
            })
            .collect(Collectors.toSet());

        System.out.println("Modified moves: " + modifiedMoves.toString());

        final Map<String, Long> existingMovesetCounts = this.battleResultEntityService.getMovesetCountsPerSpecies();

        final Map<String, SpeciesEntity> existingSpecies = this.speciesEntityService.getAllSpeciesEntitiesWithMoveIds().stream()
            .collect(Collectors.toMap(se -> se.getSpeciesId(), Function.identity()));

        Set<Species> modifiedSpecies = filteredSpecies.stream()
            .filter(species -> {
                SpeciesEntity e = existingSpecies.get(species.getSpeciesId());

                return e == null 
                    || !e.representsSpecies(species)
                    || species.moveCombinationQuantity(false) != existingMovesetCounts.getOrDefault(species.getSpeciesId(), 0L)
                    || species.getFastMoves().stream().anyMatch(modifiedMoves::contains)
                    || species.getChargedMoves().stream().anyMatch(modifiedMoves::contains);
            })
            .collect(Collectors.toSet());
        
        System.out.println("Modified species: " + modifiedSpecies.toString());
        
        final Map<String, OpponentEntity> existingOpponents = this.opponentEntityService.getAllOpponentEntitiesWithLineups().stream()
            .collect(Collectors.toMap(o -> o.getOpponentId(), Function.identity()));

        Set<Opponent> modifiedOpponents = allOpponents.stream()
            .filter(opponent -> {
                OpponentEntity e = existingOpponents.get(opponent.getOpponentId());
                return e == null 
                    || !e.representsOpponent(opponent)
                    || opponent.getLineupSpecies().stream().flatMap(s -> s.stream()).anyMatch(modifiedSpecies::contains);
            })
            .collect(Collectors.toSet());
        
        System.out.println("Modified opponents: " + modifiedOpponents.toString());

        if (!this.reconcileEntities) {
            System.out.println("Reconciliation skipped due to RECONCILE_ENTITIES env variable");
            if (!modifiedMoves.isEmpty() || !modifiedSpecies.isEmpty() || !modifiedOpponents.isEmpty()) {
                System.out.println("WARNING: in-memory objects don't match database. Reconcile to fix.");
            }
            return;
        }

        modifiedMoves.forEach(this.moveEntityService::saveMove);
        modifiedSpecies.forEach(this.speciesEntityService::saveSpecies);
        modifiedOpponents.forEach(this.opponentEntityService::saveOpponent);

        final long remainingOpponents = allOpponents.stream().filter(opponent -> !modifiedOpponents.contains(opponent)).count();
        final int totalMatchups = (modifiedOpponents.size() * filteredSpecies.size()) + ((int) remainingOpponents * modifiedSpecies.size());
        int reconciledMatchups = 0;

        for (Opponent opponent : modifiedOpponents) {
            for (Species species : filteredSpecies) {
                reconciledMatchups++;
                printReconciliationProgress(reconciledMatchups, totalMatchups, species, opponent);
                List<BattleResult> results = battleReconciliationService.createBattleResultEntities(species, opponent);
                battleReconciliationService.persistBattles(results);
            }
        }

        for (Opponent opponent : allOpponents) {
            if (modifiedOpponents.contains(opponent)) continue; //avoid battles reconciled in previous loop

            for (Species species : modifiedSpecies) {
                reconciledMatchups++;
                printReconciliationProgress(reconciledMatchups, totalMatchups, species, opponent);
                List<BattleResult> results = battleReconciliationService.createBattleResultEntities(species, opponent);
                battleReconciliationService.persistBattles(results);
            }
        }

        if (totalMatchups > 0) System.out.println();
        System.out.println("Reconciliation complete!");
    }

    private static void printReconciliationProgress(
        int reconciledMatchups,
        int totalMatchups,
        Species species,
        Opponent opponent
    ) {
        final int barWidth = 24;
        int filledWidth = totalMatchups == 0 ? barWidth : (reconciledMatchups * barWidth) / totalMatchups;
        String bar = "█".repeat(filledWidth) + "▒".repeat(barWidth - filledWidth);

        System.out.print(String.format("\033[2K\033[1GReconciling: %s | %d/%d | %s vs. %s", 
            bar, 
            reconciledMatchups, 
            totalMatchups, 
            species.getSpeciesName(), 
            opponent.getName())
        );
        System.out.flush();
    }
}
