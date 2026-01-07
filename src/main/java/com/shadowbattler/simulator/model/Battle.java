package com.shadowbattler.simulator.model;

import java.util.List;

public class Battle {
    final private List<State> states;

    final private static int buffDivisor = 4;
    final private static int maxBuffStages = 4;

    public enum DamageMultiplier {
        BONUS(1.2999999523162841796875),
        // SUPER_EFFECTIVE(1.60000002384185791015625),
        // RESISTED(0.625),
        // DOUBLE_RESISTED(0.390625),
        STAB(1.2000000476837158203125),
        SHADOW_ATK(1.2),
        SHADOW_DEF(0.83333331);

        private final double multiplier;

        private DamageMultiplier(double multiplier) {
            this.multiplier = multiplier;
        }

        public double get() {
            return this.multiplier;
        }
    }

    public static class BattlingCreature {
        final private Creature creature;
        private int remainingHp;
        private int energy;
        private int atkBuff;
        private int defBuff;

        public BattlingCreature(Creature creature) {
            this.creature = creature;
            this.remainingHp = (int)Math.round(creature.getStats().getHp());
            this.energy = 0;
            this.atkBuff = 0;
            this.defBuff = 0;
        }

        public BattlingCreature(BattlingCreature other) {
            this.creature = other.creature;
            this.remainingHp = other.remainingHp;
            this.energy = other.energy;
            this.atkBuff = other.atkBuff;
            this.defBuff = other.defBuff;
        }

        public int getRemainingHp() {
            return this.remainingHp;
        }

        public int getEnergy() {
            return this.energy;
        }

        public boolean isFainted() {
            return this.remainingHp <= 0;
        }

        public void processDamage(int damage) {
            this.remainingHp = Math.max(this.remainingHp - damage, 0);
        }

        public void processBuff(Stats3<Integer> buff) {
            this.atkBuff = Math.clamp(this.atkBuff + buff.getAtk(), -Battle.maxBuffStages, Battle.maxBuffStages);
            this.defBuff = Math.clamp(this.defBuff + buff.getDef(), -Battle.maxBuffStages, Battle.maxBuffStages);
        }
        
        private double calculateEffectiveStat(Stats3.Stat stat) {
            double multiplier;

            final int buff = stat == Stats3.Stat.ATK ? this.atkBuff : this.defBuff;
            if (buff > 0) {
                multiplier = (Battle.buffDivisor + buff) / Battle.buffDivisor;
            } else {
                multiplier = Battle.buffDivisor / (Battle.buffDivisor - buff);
            }

            if (this.creature.getSpecies().isShadow()) {
                multiplier *= stat == Stats3.Stat.ATK ? Battle.DamageMultiplier.SHADOW_ATK.get() : Battle.DamageMultiplier.SHADOW_DEF.get();
            }

            return this.creature.getStats().getByEnum(stat) * multiplier;
        }
        public double calculateEffectiveAtk() { return this.calculateEffectiveStat(Stats3.Stat.ATK); }
        public double calculateEffectiveDef() { return this.calculateEffectiveStat(Stats3.Stat.DEF); }

        /**
         * calculates the damage this BattlingCreature does to the target Battling creature using the specified move
         * @param target the BattlingCreature being attacked
         * @param move the move being used on the target by the user
         * @return the damage this BattlingCreature can inflict
         */
        public int calculateDamageAgainst(BattlingCreature target, Move move) {
            return 1 + (int)Math.floor(
                move.power() *
                this.calculateEffectiveAtk()/target.calculateEffectiveDef() *
                move.type().effectivenessAgainst(target.creature.getSpecies().getTypes()) *
                (this.creature.getSpecies().givesStabTo(move) ? Battle.DamageMultiplier.STAB.get() : 1) *
                0.5 *
                Battle.DamageMultiplier.BONUS.get()
            );
        }
    }

    public static class Trainer {
        final private Team<BattlingCreature> team;
        private int shields;

        public Trainer(Team<Creature> team, int shields) {
            this.team = new Team<>(
                new BattlingCreature(team.getFirst()),
                new BattlingCreature(team.getSecond()),
                new BattlingCreature(team.getThird())
            );
            this.shields = shields;
        }

        public Trainer(Trainer other) {
            this.team = new Team<>(
                new BattlingCreature(other.team.getFirst()),
                new BattlingCreature(other.team.getSecond()),
                new BattlingCreature(other.team.getThird())
            );
            this.shields = other.shields;
        }

        public Team<BattlingCreature> getTeam() {
            return this.team;
        }

        public int getShields() {
            return this.shields;
        }
    }

    public static class State {
        final private Battle parent;
        final private Trainer player;
        final private Trainer enemy;
        private int turnsElapsed;
        private int timeElapsed;

        public State(Battle parent, Creature playerCreature, Team<Creature> opponentTeam, int opponentStartingShields) {
            this.parent = parent;
            this.player = new Trainer(new Team<>(playerCreature, null, null), 2);
            this.enemy = new Trainer(opponentTeam, opponentStartingShields);
            this.turnsElapsed = 0;
            this.timeElapsed = 0;
        }

        public State(State other) {
            this.parent = other.parent;
            this.player = new Trainer(other.player);
            this.enemy = new Trainer(other.enemy);
            this.turnsElapsed = other.turnsElapsed;
            this.timeElapsed = other.timeElapsed;
        }

        public Trainer getPlayer() {
            return this.player;
        }

        public Trainer getEnemy() {
            return this.enemy;
        }

        public int getTurnsElapsed() {
            return this.turnsElapsed;
        }

        public int getTimeElapsed() {
            return this.timeElapsed;
        }

        /**
         * creates a clone of the current state and appends it to the parent Battle
         * @return the new clone of the current state
         */
        public State branch() {
            final State other = new State(this);
            this.parent.states.add(other);
            return other;
        }
    }

    public Battle(Creature playerCreature, Team<Creature> opponentTeam, int opponentStartingShields) {
        final State initialState = new State(this, playerCreature, opponentTeam, opponentStartingShields);
        this.states = List.of(initialState);
    }
}
