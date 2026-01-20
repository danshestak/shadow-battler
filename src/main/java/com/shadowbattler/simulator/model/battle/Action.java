package com.shadowbattler.simulator.model.battle;

public enum Action {
        FAST_ATTACK(1),
        CHARGED_ATTACK1(1),
        CHARGED_ATTACK2(2),
        SWITCH1(1),
        SWITCH2(2),
        SWITCH3(3),
        STUN(1); //for stunning enemy after switching/charged attack

        private final int id;

        private Action(int id) {
            this.id = id;
        }

        public static Action getChargedAttack(int i) {
            return switch (i) {
                case 1 -> Action.CHARGED_ATTACK1;
                case 2 -> Action.CHARGED_ATTACK2;
                default -> throw new IllegalArgumentException(
                    String.format("attempted to get CHARGED_ATTACK%n which does not exist", i)
                );
            };
        }

        
        public static Action getSwitch(int i) {
            return switch (i) {
                case 1 -> Action.SWITCH1;
                case 2 -> Action.SWITCH2;
                case 3 -> Action.SWITCH3;
                default -> throw new IllegalArgumentException(
                    String.format("attempted to get SWITCH%d which does not exist", i)
                );
            };
        }

        public boolean isChargedAttack() {
            return this == Action.CHARGED_ATTACK1 || this == Action.CHARGED_ATTACK2;
        }

        public boolean isSwitch() {
            return this == Action.SWITCH1 || this == Action.SWITCH2 || this == Action.SWITCH3;
        }

        public int get() {
            return this.id;
        }
    }
