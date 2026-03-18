package com.shadowbattler.simulator.persistence.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.shadowbattler.simulator.persistence.entity.OpponentEntity;
import com.shadowbattler.simulator.persistence.repository.OpponentEntityRepository;

@Service
public class OpponentEntityService {
    private final OpponentEntityRepository opponentEntityRepository;

    public OpponentEntityService(OpponentEntityRepository opponentEntityRepository) {
        this.opponentEntityRepository = opponentEntityRepository;
    }

    public Optional<OpponentEntity> getOpponentEntityById(String id) {
        return this.opponentEntityRepository.findById(id);
    }
}
