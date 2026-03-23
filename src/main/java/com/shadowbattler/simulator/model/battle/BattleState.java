package com.shadowbattler.simulator.model.battle;

import java.util.ArrayList;
import java.util.List;

import com.shadowbattler.simulator.model.Creature;
import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.model.Team;

/**
 * the main class for solving battles. each state holds all of the information to a given battle,
 * and processes turns given each trainers' queued actions. also holds logic for queueing new actions
 * for each player.
 */
public class BattleState {
    final private Trainer player;
    final private Trainer enemy;
    private int turnsElapsed;
    private int timeElapsed;
    private boolean finished;
    final private BattleLog log;

    /** how long it takes for a turn/step to pass */
    final private static int TURN_TIME = 500;
    /** how long it takes to switch after having just switched */
    final private static int SWITCH_COOLDOWN = 45000;
    /** how long it takes for the player to charge their charged attack, including animations */
    final private static int PLAYER_CHARGE_TIME = 10000;
    /**
     * on paper, how long it takes for an enemy to charge their charged attack, but realistically this time
     * accounts for only how long animations take and how quickly a player can shield after the enemy starts
     * charging
     */
    final private static int ENEMY_CHARGE_TIME = 7000;
    /** how long it takes for the player to switch to their next creature after their active faints */
    final private static int PLAYER_FAINT_TIME = 1500;
    /** how long it takes for the enemy to switch to their next creature after their active faints */
    final private static int ENEMY_FAINT_TIME = 3500;
    /** how many turns the enemy is stunned for after a trainer switches/uses a charged move */
    final private static int STUN_TURNS = 10;

    public BattleState(Team<Creature> playerTeam, Team<Creature> opponentTeam, int opponentStartingShields, boolean isLogged) {
        this.player = new Trainer(playerTeam, 2);
        this.enemy = new Trainer(opponentTeam, opponentStartingShields);
        this.turnsElapsed = 0;
        this.timeElapsed = 0;
        this.finished = false;
        this.log = isLogged ? new BattleLog() : null;
    }
    
    public BattleState(BattleState other) {
        this.player = new Trainer(other.player);
        this.enemy = new Trainer(other.enemy);
        this.turnsElapsed = other.turnsElapsed;
        this.timeElapsed = other.timeElapsed;
        this.finished = other.finished;
        this.log = other.log != null ? new BattleLog(other.log) : null;
    }

    public Trainer getPlayer() {
        return this.player;
    }

    public Trainer getEnemy() {
        return this.enemy;
    }

    public Trainer getOpponentTo(Trainer trainer) {
        return trainer == this.player ? this.enemy : this.player;
    }

    public int getTurnsElapsed() {
        return this.turnsElapsed;
    }

    public int getTimeElapsed() {
        return this.timeElapsed;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public boolean playerWon() {
        boolean anyPlayerAlive = false;
        for (int i = 1; i <= 3; i++) {
            BattlingCreature c = player.getTeam().getByInt(i);
            if (c != null && !c.isFainted()) {
                anyPlayerAlive = true;
                break;
            }
        }
        if (!anyPlayerAlive) return false;

        for (int i = 1; i <= 3; i++) {
            BattlingCreature c = enemy.getTeam().getByInt(i);
            if (c != null && !c.isFainted()) {
                return false;
            }
        }
        return true;
    }

    public BattleLog getLog() {
        return this.log;
    }

    /**
     * queues an action for the specified user to use on the next step. should only be used if they don't already
     * have an action queued, as queueing an action will overwrite the old one and prevent the old action from
     * ever being fulfilled
     * @param action the action for the user to take
     * @param user the user to perform the action. must be the current state's player or enemy
     */
    private void queueAction(Action action, Trainer user) {
        if (action == null) return;
        
        user.setQueuedAction(action);
        user.setQueuedActionFulfills(this.turnsElapsed + switch (action) {
            case Action.FAST_ATTACK -> user.getActive().getCreature().getFastMove().turns();
            case Action.STUN -> BattleState.STUN_TURNS-1; //-1 to account for the action being processed
            default -> 1; //switch, charged attack
        });
    }

    /**
     * helper method for calling queueAction on both of a state's trainers. does not
     * introduce any logic not found in queueAction other than specifying the user for each action
     * @param playerAction the action for this state's player to queue
     * @param enemyAction the action for this state's enemy to queue
     */
    private void queueActions(Action playerAction, Action enemyAction) {
        this.queueAction(playerAction, this.player);
        this.queueAction(enemyAction, this.enemy);
    }

    /**
     * consumes and processes the user's queued action. should not be used outside of processQueuedActions,
     * as that method considers ties and priority, and handles fainting
     * @param user the trainer who will use their queued action
     */
    private void processQueuedAction(Trainer user) {
        if (user.getQueuedAction() == null) return;

        final Trainer opp = this.getOpponentTo(user);
        final int beforeHp = opp.getActive().getRemainingHp();

        if (user.getQueuedAction().isSwitch()) {
            //forced switch after fainting has no effect on cooldown
            if (!user.getActive().isFainted()) {
                user.setSwitchCooldownEnds(this.timeElapsed + BattleState.SWITCH_COOLDOWN);
            } 
            user.switchTo(user.getQueuedAction().get());

            this.enemy.setStunQueued(true);
        } else if (user.getQueuedAction().isChargedAttack()) {
            //always uses shields if available
            /*
            the enemy ai always shields if they have them available, and for the player there
            are very few cases where not shielding is worth it. to reduce branches, the
            player will also always shield when available
            
            creating a separate branch for not shielding may be implemented in the future
            */
            final Move chargedMove = user.getActive().getCreature().getChargedMoves().get(user.getQueuedAction().get()-1);
            if (opp.getShields() > 0) {
                opp.adjustShields(-1);
                user.getActive().adjustEnergy(-chargedMove.energy());
            } else {
                user.getActive().attack(
                    opp.getActive(),
                    user.getActive().getCreature().getChargedMoves().get(user.getQueuedAction().get()-1),
                    user.getAtkBuff(),
                    opp.getDefBuff()
                );
            }
            user.applyMoveBuffsChance(chargedMove, opp);

            //apply floating fast moves
            if (opp.getQueuedActionFulfills() <= this.turnsElapsed && !opp.getActive().isFainted()) {
                this.processQueuedAction(opp);
            }
            
            //account for charge time 
            this.timeElapsed += user == this.player ? BattleState.PLAYER_CHARGE_TIME : BattleState.ENEMY_CHARGE_TIME;

            this.enemy.setStunQueued(true);
        } else if (user.getQueuedAction() == Action.FAST_ATTACK) {
            user.getActive().attack(
                opp.getActive(),
                user.getActive().getCreature().getFastMove(),
                user.getAtkBuff(),
                opp.getDefBuff()
            );
        } else if (user.getQueuedAction() == Action.STUN) {
            user.setStunQueued(false);
        }

        if (this.log != null && user.getQueuedAction() != null) {
            this.log.addEntry(
                this, 
                user, 
                user.getQueuedAction(), 
                beforeHp, 
                opp.getActive().getRemainingHp()
            );
        }

        user.setQueuedAction(null);
        user.setQueuedActionFulfills(0);
    }
    
    /**
     * consumes and processes both of the state's trainers' actions through processQueuedAction,
     * resolves ties and move priority, handles trainer's remaining creature count after one faints,
     * and marks the state as finished if a trainer runs out of creatures
     */
    private void processQueuedActions() {
        final Action playerFulfilledAction = this.player.getQueuedActionFulfills() <= this.turnsElapsed ? this.player.getQueuedAction() : null;
        final Action enemyFulfilledAction = this.enemy.getQueuedActionFulfills() <= this.turnsElapsed ? this.enemy.getQueuedAction() : null;

        if (playerFulfilledAction != null && enemyFulfilledAction != null) {
            boolean playerPriority = false;
            boolean considerPriority = true;

            BattlingCreature playerActive = this.player.getActive();
            BattlingCreature enemyActive = this.enemy.getActive();
            
            if (playerFulfilledAction.isSwitch() ^ enemyFulfilledAction.isSwitch()) {
                //one trainer is switching and the other isnt
                //switching has priority over every other action
                playerPriority = playerFulfilledAction.isSwitch();
            } else if (playerFulfilledAction.isChargedAttack() && enemyFulfilledAction.isChargedAttack()) {
                //cmp tie
                //assuming pve battles are like pvp, cmp ties use creature attack rather than effective attack
                //treating equal atk as cmp tie loss for player to avoid randomness
                playerPriority = playerActive.getCreature().getStats().getAtk() > enemyActive.getCreature().getStats().getAtk();
            } else if (playerFulfilledAction.isChargedAttack() && !enemyFulfilledAction.isChargedAttack()) {
                /*
                with the segment(s) below, charged attack is ignored if the opponent has a lethal fast attack. 
                otherwise, the charged attack has priority. pvpoke uses this logic, however this logic is not
                used in game
                 */
                // playerPriority = !(enemyFulfilledAction == Action.FAST_ATTACK && 
                //         enemyActive.calculateDamageAgainst(
                //             playerActive, 
                //             enemyActive.getCreature().getFastMove(),
                //             enemy.getAtkBuff(),
                //             player.getDefBuff()
                //         ) >= playerActive.getRemainingHp());
                playerPriority = true;
            } else if (!playerFulfilledAction.isChargedAttack() && enemyFulfilledAction.isChargedAttack()) {
                // playerPriority = playerFulfilledAction == Action.FAST_ATTACK && 
                //     playerActive.calculateDamageAgainst(
                //         enemyActive, 
                //         playerActive.getCreature().getFastMove(),
                //         player.getAtkBuff(),
                //         enemy.getDefBuff()
                //     ) >= enemyActive.getRemainingHp();
                playerPriority = false;
            } else {
                considerPriority = false;
            }

            if (considerPriority) {
                if (playerPriority) {
                    this.processQueuedAction(this.player);
                    if (!enemyActive.isFainted()) this.processQueuedAction(this.enemy);
                } else {
                    this.processQueuedAction(this.enemy);
                    if (!playerActive.isFainted()) this.processQueuedAction(this.player);
                }
            } else {
                this.processQueuedAction(this.player);
                this.processQueuedAction(this.enemy);
            }
        } else if (playerFulfilledAction != null && enemyFulfilledAction == null) {
            this.processQueuedAction(this.player);
        } else if (playerFulfilledAction == null && enemyFulfilledAction != null) {
            this.processQueuedAction(this.enemy);
        }

        if (playerFulfilledAction != null || enemyFulfilledAction != null) {
            this.checkTrainerFaint(this.player);
            this.checkTrainerFaint(this.enemy);
        }

        //end battle after 12 minutes
        if (this.timeElapsed > 720000) {
            this.finished = true;
        }
    }

    private void checkTrainerFaint(Trainer trainer) {
        if (!trainer.getActive().isFainted()) return;
        trainer.adjustRemainingCreatures(-1);

        if (trainer.getRemainingCreatures() <= 0) {
            this.finished = true;
        }

        trainer.setQueuedAction(null);
        trainer.setQueuedActionFulfills(0);   
    }

    /**
     * handles all logic for a single turn in a battle. first processes queued actions, decides which actions 
     * each trainer will take, creates branching states for each decision the player can make, and iterates 
     * the stepElapsed and timeElapsed
     * @return a list of new states created from branching player decisions. the current state is also advanced
     */
    public List<BattleState> step() {
        this.processQueuedActions();
        if (this.finished) return new ArrayList<>();

        final List<BattleState> newBranches = new ArrayList<>();

        final BattlingCreature playerActive = this.player.getActive();
        final BattlingCreature enemyActive = this.enemy.getActive();
        final Action enemyAction;

        if (enemyActive.isFainted() || playerActive.isFainted()) {
            if (enemyActive.isFainted()) {
                //switch to next slot
                enemyAction = Action.getSwitch(this.enemy.getActiveSlot() + 1);
                //account for time for enemy to switch
                this.timeElapsed += BattleState.ENEMY_FAINT_TIME;
            } else {
                enemyAction = null;
            }
            
            if (playerActive.isFainted()) {
                //account for time for player to switch without double counting
                if (enemyAction != null && !enemyAction.isSwitch()) {
                    this.timeElapsed += BattleState.PLAYER_FAINT_TIME;
                }

                int firstSwitch = -1;
                for (int i = 1; i <= 3; i++) {
                    final BattlingCreature ithSlot = this.player.getTeam().getByInt(i);
                    if (ithSlot == null || ithSlot.isFainted()) continue;

                    if (firstSwitch < 0) {
                        firstSwitch = i;
                    } else {
                        final BattleState branch = new BattleState(this);
                        branch.queueActions(Action.getSwitch(i), enemyAction);
                        newBranches.add(branch);
                    }
                }
                //current State handles first possible switch, other is on another branch
                this.queueActions(Action.getSwitch(firstSwitch), enemyAction);
            } else {
                this.queueActions(null, enemyAction);
            }

            //turns are not elapsed when switching due to a creature fainting
        } else {
            if (this.enemy.getQueuedAction() == null) {
                if (enemyActive.getEnergy() >= enemyActive.getCreature().getChargedMoves().get(0).energy()) {
                    //always uses charged move when available
                    enemyAction = Action.CHARGED_ATTACK1;
                } else if (this.enemy.hasStunQueued()) {
                    enemyAction = Action.STUN;
                    this.enemy.setStunQueued(false);
                } else {
                    enemyAction = Action.FAST_ATTACK;
                }
            } else {
                /*
                if the enemy hasStunQueued set while they are already stunned, restart the stun duration
                now instead of stunning them again after their current stun fulfills
                */
                if (this.enemy.hasStunQueued() && this.enemy.getQueuedAction() == Action.STUN) {
                    enemyAction = Action.STUN;
                    this.enemy.setStunQueued(false);
                } else {
                    enemyAction = null;
                }
            }

            if (this.player.getQueuedAction() == null) {
                for (int i = 1; i <= Math.min(playerActive.getCreature().getChargedMoves().size(), 2); i++) {
                    if (playerActive.getEnergy() >= playerActive.getCreature().getChargedMoves().get(i-1).energy()) {
                        final BattleState branch = new BattleState(this);
                        branch.queueActions(Action.getChargedAttack(i), enemyAction);
                        newBranches.add(branch);
                    } 
                }
                
                //always processes fast attack on current State, other actions are processed on new branches
                this.queueActions(Action.FAST_ATTACK, enemyAction);
            } else {
                this.queueActions(null, enemyAction);
            }
            
            this.timeElapsed += BattleState.TURN_TIME;
        }

        this.turnsElapsed++;

        return newBranches;
    }

    private long getCreatureStatusCode(BattlingCreature c) {
        if (c == null) return 0;
        if (c.isFainted()) return 1;
        return 2; // Creature is active and not fainted
    }

    /**
     * for two states to be able to be comparable, they must both return the same comparisonKey
     * from this method. if they are comparable, then they can be compared using the isDominatedBy
     * method
     * 
     * the key is a long with its bits representing the following data:
     * - bits 0-11:  turnsElapsed (up to 4095)
     * - bits 12-13: playerActiveSlot (1-3)
     * - bits 14-15: enemyActiveSlot (1-3)
     * - bits 16-17: playerRemaining (0-3)
     * - bits 18-19: enemyRemaining (0-3)
     * - bits 20-31: Status for all 6 creatures (2 bits: null, fainted, or active)
     * @return
     */
    public long getComparisonKey() {
        long key = 0L;
        key |= (long) (this.turnsElapsed & 0xFFF);
        key |= (long) (this.player.getActiveSlot() & 0x3) << 12;
        key |= (long) (this.enemy.getActiveSlot() & 0x3) << 14;
        key |= (long) (this.player.getRemainingCreatures() & 0x3) << 16;
        key |= (long) (this.enemy.getRemainingCreatures() & 0x3) << 18;
        key |= getCreatureStatusCode(this.player.getTeam().getFirst()) << 20;
        key |= getCreatureStatusCode(this.player.getTeam().getSecond()) << 22;
        key |= getCreatureStatusCode(this.player.getTeam().getThird()) << 24;
        key |= getCreatureStatusCode(this.enemy.getTeam().getFirst()) << 26;
        key |= getCreatureStatusCode(this.enemy.getTeam().getSecond()) << 28;
        key |= getCreatureStatusCode(this.enemy.getTeam().getThird()) << 30;
        return key;
    }

    /**
     * determines if this battle state is strictly dominated by another. a state is comparable if
     * it is not better in any aspect, and is worse in at least one aspect. time elapsed, shields, 
     * buffs, and creature hp and energy are all considered. to reduce overhead, this method does
     * not do any checks at runtime to see if states are comparable. to check that, compare these
     * states' comparison keys from the getComparisonKey method
     * @param other the state to compare against
     * @return true if this state is dominated by the other, false otherwise. also returns false if the
     * states are not comparable. Note: this implementation uses weak dominance, meaning a state
     * is considered dominated if it is not strictly better than the other state in any aspect.
     * This includes being equal, which allows for pruning identical states.
     */
    public boolean isDominatedBy(BattleState other) {
        //checks to see if states are comparable are commented out

        // if (this.turnsElapsed != other.turnsElapsed ||
        //     this.player.getActiveSlot() != other.player.getActiveSlot() ||
        //     this.enemy.getActiveSlot() != other.enemy.getActiveSlot() ||
        //     this.player.getRemainingCreatures() != other.player.getRemainingCreatures() ||
        //     this.enemy.getRemainingCreatures() != other.enemy.getRemainingCreatures()
        // ) {
        //     return false;
        // }

        // for (int i = 1; i <= 3; i++) {
        //     BattlingCreature thisPC = this.player.getTeam().getByInt(i);
        //     BattlingCreature otherPC = other.player.getTeam().getByInt(i);
        //     if ((thisPC == null) != (otherPC == null) || (thisPC != null && thisPC.isFainted() != (otherPC != null && otherPC.isFainted()))) {
        //         return false;
        //     }

        //     BattlingCreature thisEC = this.enemy.getTeam().getByInt(i);
        //     BattlingCreature otherEC = other.enemy.getTeam().getByInt(i);
        //     if ((thisEC == null) != (otherEC == null) || (thisEC != null && thisEC.isFainted() != (otherEC != null && otherEC.isFainted()))) {
        //         return false;
        //     }
        // }

        if (this.timeElapsed < other.timeElapsed) return false;
        if (this.player.getShields() > other.player.getShields()) return false;
        if (this.enemy.getShields() < other.enemy.getShields()) return false;
        if (this.player.getAtkBuff() > other.player.getAtkBuff()) return false;
        if (this.player.getDefBuff() > other.player.getDefBuff()) return false;
        if (this.enemy.getAtkBuff() < other.enemy.getAtkBuff()) return false;
        if (this.enemy.getDefBuff() < other.enemy.getDefBuff()) return false;

        for (int i = 1; i <= 3; i++) {
            BattlingCreature thisPC = this.player.getTeam().getByInt(i);
            if (thisPC != null && !thisPC.isFainted()) {
                BattlingCreature otherPC = other.player.getTeam().getByInt(i);
                if (thisPC.getRemainingHp() > otherPC.getRemainingHp()) return false;
                if (thisPC.getEnergy() > otherPC.getEnergy()) return false;
            }

            BattlingCreature thisEC = this.enemy.getTeam().getByInt(i);
            if (thisEC != null && !thisEC.isFainted()) {
                BattlingCreature otherEC = other.enemy.getTeam().getByInt(i);
                if (thisEC.getRemainingHp() < otherEC.getRemainingHp()) return false;
                if (thisEC.getEnergy() < otherEC.getEnergy()) return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BattleState{");
        sb.append("player=").append(player);
        sb.append(", enemy=").append(enemy);
        // sb.append(", trainers=").append(trainers);
        sb.append(", turnsElapsed=").append(turnsElapsed);
        sb.append(", timeElapsed=").append(timeElapsed);
        sb.append(", finished=").append(finished);
        sb.append('}');
        return sb.toString();
    }
}
