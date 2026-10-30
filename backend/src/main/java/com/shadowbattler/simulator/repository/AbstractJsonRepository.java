package com.shadowbattler.simulator.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

public abstract class AbstractJsonRepository<T> implements Repository<T, String> {
    private final ObjectMapper objectMapper;
    private final String fileName;
    private final TypeReference<List<T>> typeReference;
    private final Function<T, String> idExtractor;
    @Value("${USE_TEST_JSON:true}")
    private boolean useTestJson;
    protected Map<String, T> cache = new HashMap<>();

    protected AbstractJsonRepository(
        ObjectMapper objectMapper, 
        String fileName, 
        TypeReference<List<T>> typeReference, 
        Function<T, String> idExtractor
    ) {
        this.objectMapper = objectMapper;
        this.fileName = fileName;
        this.typeReference = typeReference;
        this.idExtractor = idExtractor;
    }

    @PostConstruct
    public void loadAll() {
        try {
            InputStream inputStream = new ClassPathResource(
                String.format("/json/%s/%s", this.useTestJson ? "test" : "prod", this.fileName)
            ).getInputStream();
            List<T> items = objectMapper.readValue(inputStream, this.typeReference);
            
            this.cache = items.stream().collect(Collectors.toMap(this.idExtractor, Function.identity()));
                
            System.out.println("Loaded " + this.cache.size() + " items from " + this.fileName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load data from " + this.fileName, e);
        }
    }

    @Override
    public Optional<T> findById(String id) {
        return Optional.ofNullable(this.cache.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(this.cache.values());
    }

    @Override
    public Map<String, T> findAllAsMap() {
        return this.cache;
    }
}
