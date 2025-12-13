package com.shadowbattler.simulator.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shadowbattler.simulator.model.species.Species;
import com.shadowbattler.simulator.service.SpeciesDataService;

@RestController
@RequestMapping("/api/species")
public class SpeciesController {
    private final SpeciesDataService speciesDataService;

    public SpeciesController(SpeciesDataService speciesDataService) {
        this.speciesDataService = speciesDataService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Species> getPokemon(@PathVariable String id) {
        Species species = this.speciesDataService.getSpeciesById(id);
        if (species == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(species);
    }
}
