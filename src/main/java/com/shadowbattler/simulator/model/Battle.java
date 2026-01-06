package com.shadowbattler.simulator.model;

import java.util.List;

public class Battle {
    final private List<State> states;

    public class Trainer {
        final private Team<Creature> team;
        private int shields;

        public Trainer(Team<Creature> team, int shields) {
            this.team = team;
            this.shields = shields;
        }
    }

    public class State {
        final private Trainer player;
        final private Trainer enemy;
        private int turnsElapsed;
        private int timeElapsed;

        public State(Creature playerCreature, Team<Creature> opponentTeam, int opponentStartingShields) {
            this.player = new Trainer(new Team<>(playerCreature, null, null), 2);
            this.enemy = new Trainer(opponentTeam, opponentStartingShields);
            this.turnsElapsed = 0;
            this.timeElapsed = 0;
        }
    }

    public Battle(Creature playerCreature, Team<Creature> opponentTeam, int opponentStartingShields) {
        final State initialState = new State(playerCreature, opponentTeam, opponentStartingShields);
        this.states = List.of(initialState);
    }
}
