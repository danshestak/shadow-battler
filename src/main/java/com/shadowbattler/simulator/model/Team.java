package com.shadowbattler.simulator.model;

public record Team<T>(
    T first,
    T second,
    T third
) {}
