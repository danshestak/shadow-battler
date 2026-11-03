package com.shadowbattler.simulator.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.shadowbattler.simulator.model.Creature;
import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.model.Opponent;
import com.shadowbattler.simulator.model.Species;
import com.shadowbattler.simulator.model.Stats3;
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
    @Value("${RECONCILE_ENTITIES:false}")
    private boolean reconcileEntities;
    private final AtomicBoolean reconciliationRunning = new AtomicBoolean(false);
    private volatile ReconciliationStatus status = ReconciliationStatus.IDLE;
    private volatile String statusMessage = "Reconciliation has not been run";
    private volatile int reconciledMatchups;
    private volatile int totalMatchups;
    private volatile String currOpponentId;
    private volatile String currSpeciesId;

    public enum ReconciliationStatus {
        IDLE,
        RUNNING,
        COMPLETE,
        FAILED
    }

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

    private static final List<String> speciesBlacklist = List.of("smeargle", "morpeko", "aegislash", "mimikyu", "pikachu");

    public static boolean speciesFilter(Species species) {
        if (species.getFamily() != null && species.getFamily().evolutions() != null)
            return false;
        if (!species.isReleased())
            return false;
        if (new Creature(species, Stats3.getMaxIVs(), 50, null, null).getCp() < 2500)
            return false;
        return !speciesBlacklist.stream().anyMatch(species.getSpeciesId()::startsWith);
    }

    public boolean startReconciliation() {
        if (!this.reconcileEntities) {
            this.status = ReconciliationStatus.IDLE;
            this.statusMessage = "Reconciliation disabled by RECONCILE_ENTITIES";
            System.out.println(this.statusMessage);
            return false;
        }

        if (!reconciliationRunning.compareAndSet(false, true)) {
            throw new IllegalStateException("Reconciliation is already running");
        }

        this.reconciledMatchups = 0;
        this.totalMatchups = 0;
        this.status = ReconciliationStatus.RUNNING;
        this.statusMessage = "Reconciliation started";
        return true;
    }

    @Async
    public void reconcileModified() {
        if (!reconciliationRunning.get()) {
            throw new IllegalStateException("Reconciliation must be started through startReconciliation()");
        }

        try {
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

            final Map<String, SpeciesEntity> existingSpecies = this.speciesEntityService
                .getAllSpeciesEntitiesWithMoveIds().stream()
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

            final Map<String, OpponentEntity> existingOpponents = this.opponentEntityService
                .getAllOpponentEntitiesWithLineups().stream()
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

            modifiedMoves.forEach(this.moveEntityService::saveMove);
            modifiedSpecies.forEach(this.speciesEntityService::saveSpecies);
            modifiedOpponents.forEach(this.opponentEntityService::saveOpponent);

            final long remainingOpponents = allOpponents.stream() .filter(opponent -> !modifiedOpponents.contains(opponent)).count();

            this.totalMatchups = (modifiedOpponents.size() * filteredSpecies.size()) + ((int) remainingOpponents * modifiedSpecies.size());
            System.out.println(String.format("Reconciling %d matchups...", this.totalMatchups));

            for (Opponent opponent : modifiedOpponents) {
                for (Species species : filteredSpecies) {
                    this.reconciledMatchups++;
                    this.currOpponentId = opponent.getOpponentId();
                    this.currSpeciesId = species.getSpeciesId();

                    printReconciliationProgress(this.reconciledMatchups, this.totalMatchups);
                    List<BattleResult> results = battleReconciliationService.createBattleResultEntities(species, opponent);
                    battleReconciliationService.persistBattles(results);
                }
            }

            for (Opponent opponent : allOpponents) {
                if (modifiedOpponents.contains(opponent)) continue; // avoid battles reconciled in previous loop

                for (Species species : modifiedSpecies) {
                    this.reconciledMatchups++;
                    this.currOpponentId = opponent.getOpponentId();
                    this.currSpeciesId = species.getSpeciesId();

                    printReconciliationProgress(this.reconciledMatchups, this.totalMatchups);
                    List<BattleResult> results = battleReconciliationService.createBattleResultEntities(species, opponent);
                    battleReconciliationService.persistBattles(results);
                }
            }

            this.status = ReconciliationStatus.COMPLETE;
            this.statusMessage = "Reconciliation complete";
            System.out.println(this.statusMessage);
        } catch (RuntimeException exception) {
            this.status = ReconciliationStatus.FAILED;
            this.statusMessage = "Reconciliation failed: " + exception.getMessage();
        } finally {
            reconciliationRunning.set(false);
        }
    }

    public Map<String, Object> getReconciliationStatus() {
        return Map.of(
            "status", status.name(),
            "message", this.statusMessage,
            "processed", this.reconciledMatchups,
            "total", this.totalMatchups,
            "opponent", this.currOpponentId,
            "species", this.currSpeciesId
        );
    }

    private static void printReconciliationProgress(int reconciledMatchups, int totalMatchups) {
        if (totalMatchups <= 0) return;

        int curr = reconciledMatchups * 100 / totalMatchups;
        int prev = (reconciledMatchups - 1) * 100 / totalMatchups;
        if (curr > prev) {
            System.out.println(String.format("Reconciliation %d%% complete (%d/%d)", curr, reconciledMatchups, totalMatchups));
        }
    }
}
