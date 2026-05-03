package com.shadowbattler.simulator.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shadowbattler.exception.ResourceNotFoundException;
import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.repository.MovesRepository;

@Service
public class MovesDataService {
    private final MovesRepository movesRepository;

    public MovesDataService(MovesRepository movesRepository) {
        this.movesRepository = movesRepository;
    }

    public Move getMoveById(String id) {
        return movesRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException(String.format("move of id %s not found", id)));
    }

    public List<Move> getAllMoves() {
        return this.movesRepository.findAll();
    }
}
