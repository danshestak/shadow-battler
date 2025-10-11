package com.shadowbattler.simulator.model.species;

import java.util.Arrays;

public class Learnset {
    private final String[] fastMoves;
    private final String[] chargedMoves;
    private final String[] eliteMoves;
    
    public Learnset(String[] chargedMoves, String[] eliteMoves, String[] fastMoves) {
        this.chargedMoves = Arrays.copyOf(chargedMoves, chargedMoves.length);
        this.eliteMoves = Arrays.copyOf(eliteMoves, eliteMoves.length);
        this.fastMoves = Arrays.copyOf(fastMoves, fastMoves.length);
    }

    public String[] getFastMoves() {
        return Arrays.copyOf(fastMoves, fastMoves.length);
    }

    public String[] getChargedMoves() {
        return Arrays.copyOf(chargedMoves, chargedMoves.length);
    }

    public String[] getEliteMoves() {
        return Arrays.copyOf(eliteMoves, eliteMoves.length);
    }
}
