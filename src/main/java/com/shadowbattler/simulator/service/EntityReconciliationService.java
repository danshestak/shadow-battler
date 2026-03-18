package com.shadowbattler.simulator.service;

import org.springframework.stereotype.Service;

import com.shadowbattler.simulator.persistence.service.BattleResultEntityService;
import com.shadowbattler.simulator.persistence.service.MoveEntityService;
import com.shadowbattler.simulator.persistence.service.OpponentEntityService;
import com.shadowbattler.simulator.persistence.service.SpeciesEntityService;

@Service
public class EntityReconciliationService {
    private final BattleResultEntityService battleResultEntityService;
    private final SpeciesEntityService speciesEntityService;
    private final SpeciesDataService speciesDataService;
    private final OpponentEntityService opponentEntityService;
    private final OpponentDataService opponentDataService;
    private final MoveEntityService moveEntityService;
    private final MovesDataService movesDataService;

    public EntityReconciliationService(
        BattleResultEntityService battleResultEntityService,
        SpeciesEntityService speciesEntityService,
        SpeciesDataService speciesDataService,
        OpponentEntityService opponentEntityService,
        OpponentDataService opponentDataService,
        MoveEntityService moveEntityService,
        MovesDataService movesDataService
    ) {
        this.battleResultEntityService = battleResultEntityService;
        this.speciesEntityService = speciesEntityService;
        this.speciesDataService = speciesDataService;
        this.opponentEntityService = opponentEntityService;
        this.opponentDataService = opponentDataService;
        this.moveEntityService = moveEntityService;
        this.movesDataService = movesDataService;
    }

    public void reconcile() {
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
}
