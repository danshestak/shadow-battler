package com.shadowbattler.simulator.persistence.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<BattleResultEntity> saveAll(List<BattleResultEntity> battleResultEntities) {
        return this.battleResultEntityRepository.saveAll(battleResultEntities);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getMovesetCountsPerSpecies() {
        List<Object[]> results = this.battleResultEntityRepository.findDistinctMovesetsPerSpecies();
        return results.stream()
            .collect(Collectors.groupingBy(
                row -> (String) row[0],
                Collectors.counting()
            ));
    }
}
