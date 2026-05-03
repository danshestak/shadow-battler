package com.shadowbattler.simulator.persistence.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.persistence.entity.MoveEntity;
import com.shadowbattler.simulator.persistence.repository.MoveEntityRepository;

@Service
public class MoveEntityService {
    private final MoveEntityRepository moveEntityRepository;

    public MoveEntityService(MoveEntityRepository moveEntityRepository) {
        this.moveEntityRepository = moveEntityRepository;
    }

    public List<MoveEntity> getAllMoveEntities() {
        return this.moveEntityRepository.findAll();
    }

    public Optional<MoveEntity> getMoveEntityById(String id) {
        return this.moveEntityRepository.findById(id);
    }

    public MoveEntity getReferenceById(String id) {
        return this.moveEntityRepository.getReferenceById(id);
    }

    public MoveEntity saveMove(Move move) {
        MoveEntity entity = this.moveEntityRepository.findById(move.moveId()).orElse(new MoveEntity());
        
        entity.updateFromMove(move);
        //triggers orphan removal
        entity.getBattleResultsAsFastMove().clear();
        entity.getBattleResultsAsChargedMove1().clear();
        entity.getBattleResultsAsChargedMove2().clear();
        return this.moveEntityRepository.save(entity);
    }
}
