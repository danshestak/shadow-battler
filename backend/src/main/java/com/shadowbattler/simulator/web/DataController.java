package com.shadowbattler.simulator.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.model.Opponent;
import com.shadowbattler.simulator.model.Species;
import com.shadowbattler.simulator.persistence.entity.BattleResultEntity;
import com.shadowbattler.simulator.persistence.service.BattleResultEntityService;
import com.shadowbattler.simulator.service.BattlePersistenceService;
import com.shadowbattler.simulator.service.EntityReconciliationService;
import com.shadowbattler.simulator.service.MovesDataService;
import com.shadowbattler.simulator.service.OpponentDataService;
import com.shadowbattler.simulator.service.SpeciesDataService;

@RestController
@RequestMapping("/api")
public class DataController {
    private final SpeciesDataService speciesDataService;
    private final MovesDataService movesDataService;
    private final OpponentDataService opponentDataService;
    private final BattleResultEntityService battleResultEntityService;

    private final Map<String, List<?>> countersConfig;

    public DataController(
        SpeciesDataService speciesDataService, 
        MovesDataService movesDataService,
        OpponentDataService opponentDataService,
        BattleResultEntityService battleResultEntityService
    ) {
        this.speciesDataService = speciesDataService;
        this.movesDataService = movesDataService;
        this.opponentDataService = opponentDataService;
        this.battleResultEntityService = battleResultEntityService;
        
        this.countersConfig = new HashMap<>();
        this.countersConfig.put("playerCreatureLevels", BattlePersistenceService.PLAYER_CREATURE_LEVELS);
        this.countersConfig.put("trainerLevels", BattlePersistenceService.TRAINER_LEVELS.stream().map(i -> (double)i).toList());
        this.countersConfig.put("filteredSpecies", speciesDataService.getAllSpeciesMap().values().stream().filter(EntityReconciliationService::speciesFilter).map(s -> s.getSpeciesId()).toList());
    }

    @GetMapping("/species")
    public ResponseEntity<Map<String, Species>> getAllSpecies(WebRequest request) {
        return cachedResponse(request, "species-" + this.speciesDataService.getRepoHash(), this.speciesDataService::getAllSpeciesMap);
    }

    @GetMapping("/moves")
    public ResponseEntity<Map<String, Move>> getAllMoves(WebRequest request) {
        return cachedResponse(request, "moves-" + this.movesDataService.getRepoHash(), this.movesDataService::getAllMovesMap);
    }

    @GetMapping("/opponents")
    public ResponseEntity<Map<String, Opponent>> getAllOpponents(WebRequest request) {
        return cachedResponse(request, "opponents-" + this.opponentDataService.getRepoHash(), this.opponentDataService::getAllOpponentsMap);
    }

    @GetMapping("/counters/{opponentId}")
    public List<BattleResultEntity> getCounters(@PathVariable String opponentId) {
        return this.battleResultEntityService.getTopScoringBRsPerSpecies(opponentId);
    }

    @GetMapping("/counters_config")
    public Map<String, List<?>> getCountersConfig() {
        return this.countersConfig;
    }

    private <T> ResponseEntity<T> cachedResponse(WebRequest request, String etag, Supplier<T> bodySupplier) {
        if (request.checkNotModified(etag)) return null;

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .eTag(etag)
            .body(bodySupplier.get());
    }
}
