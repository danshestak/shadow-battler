package com.shadowbattler.simulator.persistence.service;

import org.springframework.stereotype.Service;

import com.shadowbattler.simulator.persistence.repository.OpponentEntityRepository;

@Service
public class OpponentEntityService {
    private final OpponentEntityRepository opponentEntityRepository;

    public OpponentEntityService(OpponentEntityRepository opponentEntityRepository) {
        this.opponentEntityRepository = opponentEntityRepository;
    }
}
