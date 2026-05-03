package com.shadowbattler.simulator.model.battle;

import java.util.ArrayList;
import java.util.List;

public class BattleLog {
    private final List<LogEntry> list;

    public static record LogEntry(
        boolean isPlayer,
        Action action,
        int beforeHp,
        int afterHp,
        int time,
        int turn
    ) {
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("[Turn ")
                .append(this.turn)
                .append(", ")
                .append(this.time/1000d)
                .append("s] ")
                .append(this.isPlayer ? "PLAYER_" : "OPPONENT_")
                .append(this.action);
            
            if (this.action == Action.FAST_ATTACK || this.action.isChargedAttack()) {
                sb.append(" (");
                if (this.beforeHp == this.afterHp && this.action.isChargedAttack()) {
                    sb.append("SHIELDED");
                } else {
                    sb.append(this.beforeHp)
                        .append("HP -> ")
                        .append(this.afterHp)
                        .append("HP");
                }
                sb.append(")");
            }
            return sb.toString();
        }
    }

    public BattleLog() {
        this.list = new ArrayList<>();
    }

    public BattleLog(BattleLog other) {
        this.list = new ArrayList<>(other.list);
    }
    
    /**
     * logs an entry performed in the given state. entries must be logged sequentially
     * @param state the state in which the action was performed
     * @param user the user who performed the action
     * @param action the action to log
     * @param beforeHp the hp of the opponent before the action
     * @param afterHp the hp of the opponent after the action
     * @throws IllegalStateException if an action is added which occurred before an already existing entry
     */
    public void addEntry(BattleState state, Trainer user, Action action, int beforeHp, int afterHp) {
        if (!list.isEmpty()) {
            final LogEntry last = this.list.getLast();
            if (last.time() > state.getTimeElapsed() || last.turn() > state.getTurnsElapsed()) {
                throw new IllegalStateException("attempted to add log entry that occurs before previous entry");
            }
        }

        this.list.add(new LogEntry(
            state.getPlayer() == user, 
            action, 
            beforeHp, 
            afterHp, 
            state.getTimeElapsed(), 
            state.getTurnsElapsed()
        ));
    }

    @Override
    public String toString() {
        return "BattleLog" + list;
    }
}
