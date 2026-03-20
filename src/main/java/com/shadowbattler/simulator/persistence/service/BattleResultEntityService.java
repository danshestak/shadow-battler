package com.shadowbattler.simulator.persistence.service;

import org.springframework.stereotype.Service;

import com.shadowbattler.simulator.persistence.entity.BattleResultEntity;
import com.shadowbattler.simulator.persistence.repository.BattleResultEntityRepository;

@Service
public class BattleResultEntityService {
    private final BattleResultEntityRepository battleResultEntityRepository;

    public BattleResultEntityService(BattleResultEntityRepository battleResultEntityRepository) {
        this.battleResultEntityRepository = battleResultEntityRepository;
    }

    public BattleResultEntity save(BattleResultEntity battleResultEntity) {
        return this.battleResultEntityRepository.save(battleResultEntity);
    }
}
