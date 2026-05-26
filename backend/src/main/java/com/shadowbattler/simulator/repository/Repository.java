package com.shadowbattler.simulator.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Repository<T, ID> {
    public Optional<T> findById(ID id);
    public List<T> findAll();
    public Map<ID, T> findAllAsMap();
}
