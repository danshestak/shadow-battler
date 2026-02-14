package com.shadowbattler.simulator.persistence.service;

import org.springframework.stereotype.Service;

import com.shadowbattler.simulator.persistence.repository.MoveEntityRepository;

@Service
public class MoveEntityService {
    private final MoveEntityRepository moveEntityRepository;

    public MoveEntityService(MoveEntityRepository moveEntityRepository) {
        this.moveEntityRepository = moveEntityRepository;
    }
}
