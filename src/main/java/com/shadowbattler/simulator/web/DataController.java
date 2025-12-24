package com.shadowbattler.simulator.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.model.Species;
import com.shadowbattler.simulator.service.MovesDataService;
import com.shadowbattler.simulator.service.SpeciesDataService;

@RestController
@RequestMapping("/api")
public class DataController {
    private final SpeciesDataService speciesDataService;
    private final MovesDataService movesDataService;

    public DataController(SpeciesDataService speciesDataService, MovesDataService movesDataService) {
        this.speciesDataService = speciesDataService;
        this.movesDataService = movesDataService;
    }

    @GetMapping("/species/{id}")
    public Species getSpecies(@PathVariable String id) {
        return this.speciesDataService.getSpeciesById(id);
    }

    @GetMapping("/move/{id}")
    public Move getMove(@PathVariable String id) {
        return this.movesDataService.getMoveById(id);
    }
}
