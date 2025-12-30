package com.shadowbattler.simulator.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.model.Species;
import com.shadowbattler.simulator.model.Trainer;
import com.shadowbattler.simulator.service.MovesDataService;
import com.shadowbattler.simulator.service.SpeciesDataService;
import com.shadowbattler.simulator.service.TrainerDataService;

@RestController
@RequestMapping("/api")
public class DataController {
    private final SpeciesDataService speciesDataService;
    private final MovesDataService movesDataService;
    private final TrainerDataService trainerDataService;

    public DataController(
        SpeciesDataService speciesDataService, 
        MovesDataService movesDataService,
        TrainerDataService trainerDataService
    ) {
        this.speciesDataService = speciesDataService;
        this.movesDataService = movesDataService;
        this.trainerDataService = trainerDataService;
    }

    @GetMapping("/species/{id}")
    public Species getSpecies(@PathVariable String id) {
        return this.speciesDataService.getSpeciesById(id);
    }

    @GetMapping("/move/{id}")
    public Move getMove(@PathVariable String id) {
        return this.movesDataService.getMoveById(id);
    }

    @GetMapping("/trainer/{id}")
    public Trainer getTrainer(@PathVariable String id) {
        return this.trainerDataService.getTrainerById(id);
    }
}
