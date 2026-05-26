package com.shadowbattler.simulator.web;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.model.Opponent;
import com.shadowbattler.simulator.model.Species;
import com.shadowbattler.simulator.service.MovesDataService;
import com.shadowbattler.simulator.service.OpponentDataService;
import com.shadowbattler.simulator.service.SpeciesDataService;

@RestController
@RequestMapping("/api")
public class DataController {
    private final SpeciesDataService speciesDataService;
    private final MovesDataService movesDataService;
    private final OpponentDataService opponentDataService;

    public DataController(
        SpeciesDataService speciesDataService, 
        MovesDataService movesDataService,
        OpponentDataService opponentDataService
    ) {
        this.speciesDataService = speciesDataService;
        this.movesDataService = movesDataService;
        this.opponentDataService = opponentDataService;
    }

    @GetMapping("/species/{id}")
    public Species getSpecies(@PathVariable String id) {
        return this.speciesDataService.getSpeciesById(id);
    }

    @GetMapping("/species")
    public Map<String, Species> getAllSpecies() {
        return this.speciesDataService.getAllSpeciesMap();
    }

    @GetMapping("/move/{id}")
    public Move getMove(@PathVariable String id) {
        return this.movesDataService.getMoveById(id);
    }

    @GetMapping("/moves")
    public Map<String, Move> getAllMoves() {
        return this.movesDataService.getAllMovesMap();
    }

    @GetMapping("/opponent/{id}")
    public Opponent getOpponent(@PathVariable String id) {
        return this.opponentDataService.getOpponentById(id);
    }

    @GetMapping("/opponents")
    public Map<String, Opponent> getAllOpponents() {
        return this.opponentDataService.getAllOpponentsMap();
    }
}
