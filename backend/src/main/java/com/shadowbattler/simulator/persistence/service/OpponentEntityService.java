package com.shadowbattler.simulator.persistence.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.shadowbattler.simulator.model.Opponent;
import com.shadowbattler.simulator.persistence.entity.OpponentEntity;
import com.shadowbattler.simulator.persistence.repository.OpponentEntityRepository;

@Service
public class OpponentEntityService {
    private final OpponentEntityRepository opponentEntityRepository;

    public OpponentEntityService(OpponentEntityRepository opponentEntityRepository) {
        this.opponentEntityRepository = opponentEntityRepository;
    }

    public List<OpponentEntity> getAllOpponentEntities() {
        return this.opponentEntityRepository.findAll();
    }

    public List<OpponentEntity> getAllOpponentEntitiesWithLineups() {
        return this.opponentEntityRepository.findAllWithLineups();
    }

    public Optional<OpponentEntity> getOpponentEntityById(String id) {
        return this.opponentEntityRepository.findById(id);
    }

    public OpponentEntity getReferenceById(String id) {
        return this.opponentEntityRepository.getReferenceById(id);
    }

    public OpponentEntity saveOpponent(Opponent opponent) {
        OpponentEntity entity = this.opponentEntityRepository.findById(opponent.getOpponentId()).orElse(new OpponentEntity());

        entity.updateFromOpponent(opponent);
        entity.getBattleResults().clear(); //triggers orphan removal
        return this.opponentEntityRepository.save(entity);
    }
}
