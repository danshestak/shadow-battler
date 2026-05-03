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

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

public abstract class AbstractJsonRepository<T> implements Repository<T, String> {
    private final ObjectMapper objectMapper;
    private final String resourcePath;
    private final TypeReference<List<T>> typeReference;
    private final Function<T, String> idExtractor;

    protected Map<String, T> cache = new HashMap<>();

    protected AbstractJsonRepository(
        ObjectMapper objectMapper, 
        String resourcePath, 
        TypeReference<List<T>> typeReference, 
        Function<T, String> idExtractor
    ) {
        this.objectMapper = objectMapper;
        this.resourcePath = resourcePath;
        this.typeReference = typeReference;
        this.idExtractor = idExtractor;
    }

    @PostConstruct
    public void loadAll() {
        try {
            InputStream inputStream = new ClassPathResource(resourcePath).getInputStream();
            List<T> items = objectMapper.readValue(inputStream, typeReference);
            
            this.cache = items.stream().collect(Collectors.toMap(idExtractor, Function.identity()));
                
            System.out.println("Loaded " + this.cache.size() + " items from " + resourcePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load data from " + resourcePath, e);
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
}
