package com.shadowbattler.simulator.persistence.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.shadowbattler.simulator.persistence.entity.MoveEntity;
import com.shadowbattler.simulator.persistence.repository.MoveEntityRepository;

@Service
public class MoveEntityService {
    private final MoveEntityRepository moveEntityRepository;

    public MoveEntityService(MoveEntityRepository moveEntityRepository) {
        this.moveEntityRepository = moveEntityRepository;
    }

    public Optional<MoveEntity> getMoveEntityById(String id) {
        return this.moveEntityRepository.findById(id);
    }
}
