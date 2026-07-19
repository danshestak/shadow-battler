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
public class OldBattleState implements BattleState {
    final private Trainer player;
    final private Trainer enemy;
    private int turnsElapsed;
    private int timeElapsed;
    private boolean finished;
    private int comparisonKey;
    final private BattleLog log;
    private int projTimeElapsedLowerBoundAddend;
    final private double[] maxDps;

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
    /** how long it takes either trainer to switch to their next creature after their active faints */
    final private static int FAINT_TIME = 1000;
    /** how many turns the enemy is stunned for after a trainer switches/uses a charged move */
    final private static int STUN_TURNS = 8;

    final protected static double BUFF_APPLY_THRESHOLD = 0.8;

    public OldBattleState(Team<Creature> playerTeam, Team<Creature> opponentTeam, int opponentStartingShields, boolean isLogged) {
        this.player = new Trainer(playerTeam, 2);
        this.enemy = new Trainer(opponentTeam, opponentStartingShields);
        this.turnsElapsed = 0;
        this.timeElapsed = 0;
        this.finished = false;
        this.comparisonKey = this.createComparisonKey();
        this.log = isLogged ? new BattleLog() : null;
        this.maxDps = OldBattleState.createMaxDpsArr(this.player.getBattlingCreatures(), this.enemy.getBattlingCreatures());
    }
    
    public OldBattleState(OldBattleState other) {
        this.player = new Trainer(other.player);
        this.enemy = new Trainer(other.enemy);
        this.turnsElapsed = other.turnsElapsed;
        this.timeElapsed = other.timeElapsed;
        this.finished = other.finished;
        this.comparisonKey = other.comparisonKey;
        this.log = other.log != null ? new BattleLog(other.log) : null;
        this.maxDps = other.maxDps;
    }

    @Override
    public Trainer getPlayer() {
        return this.player;
    }

    @Override
	public Trainer getEnemy() {
        return this.enemy;
    }

    public Trainer getOpponentTo(Trainer trainer) {
        return trainer == this.player ? this.enemy : this.player;
    }

    public int getTurnsElapsed() {
        return this.turnsElapsed;
    }

    @Override
    public int getTimeElapsed() {
        return this.timeElapsed;
    }

    @Override
    public boolean isFinished() {
        return this.finished;
    }

    @Override
    public boolean playerWon() {
        return this.player.getRemainingCreatures() > 0 && this.enemy.getRemainingCreatures() <= 0;
    }

    @Override
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
            case Action.STUN -> OldBattleState.STUN_TURNS-1; //-1 to account for the action being processed
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
     * @param isPlayer true if user is the player, false if user is the enemy
     */
    private void processQueuedAction(Trainer user, boolean isPlayer) {
        if (user.getQueuedAction() == null) return;

        final Trainer opp = isPlayer ? this.enemy : this.player;
        final int beforeHp = opp.getActive().getRemainingHp();

        if (user.getQueuedAction().isSwitch()) {
            //forced switch after fainting has no effect on cooldown
            if (!user.getActive().isFainted()) {
                user.setSwitchCooldownEnds(this.timeElapsed + OldBattleState.SWITCH_COOLDOWN);
            }
            user.switchTo(user.getQueuedAction().get());
            
            final int offset = isPlayer ? 12 : 14;
            this.comparisonKey &= ~(0x3 << offset);
            this.comparisonKey |= (user.getQueuedAction().get() << offset);

            //projTimeElapsedLowerBoundAddend changes when enemy's active changes
            if (!isPlayer) {
                this.projTimeElapsedLowerBoundAddend = this.calculateProjTimeElapsedLowerBoundAddend();
            }

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
                this.processQueuedAction(opp, !isPlayer);
            }
            
            //account for charge time 
            this.timeElapsed += isPlayer ? OldBattleState.PLAYER_CHARGE_TIME : OldBattleState.ENEMY_CHARGE_TIME;

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

        /*
        fainted creatures are forced to switch with no other action being available that turn, so assume both
        fulfilled actions are a switch or null and there is at least one switch if either creature is fainted
        */
        if (this.enemy.getActive().isFainted() || this.player.getActive().isFainted()) {
            //account for time needed to switch. an OR statement to avoid double counting if both trainers switch
            this.timeElapsed += OldBattleState.FAINT_TIME;
        }

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
                    this.processQueuedAction(this.player, true);
                    if (!enemyActive.isFainted()) this.processQueuedAction(this.enemy, false);
                } else {
                    this.processQueuedAction(this.enemy, false);
                    if (!playerActive.isFainted()) this.processQueuedAction(this.player, true);
                }
            } else {
                this.processQueuedAction(this.player, true);
                this.processQueuedAction(this.enemy, false);
            }
        } else if (playerFulfilledAction != null && enemyFulfilledAction == null) {
            this.processQueuedAction(this.player, true);
        } else if (playerFulfilledAction == null && enemyFulfilledAction != null) {
            this.processQueuedAction(this.enemy, false);
        }

        if (playerFulfilledAction != null || enemyFulfilledAction != null) {
            this.checkTrainerFaint(this.player, true);
            this.checkTrainerFaint(this.enemy, false);
        }

        //end battle after 12 minutes
        if (this.timeElapsed > 720000) {
            this.finished = true;
        }
    }

    private void checkTrainerFaint(Trainer trainer, boolean isPlayer) {
        if (!trainer.getActive().isFainted()) return;
        trainer.adjustRemainingCreatures(-1);
        this.comparisonKey |= (1 << (trainer.getActiveSlot() + (isPlayer ? 15 : 18)));

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
    @Override
    public List<OldBattleState> step() {
        this.processQueuedActions();
        if (this.finished) return new ArrayList<>();

        final List<OldBattleState> newBranches = new ArrayList<>();

        final BattlingCreature playerActive = this.player.getActive();
        final BattlingCreature enemyActive = this.enemy.getActive();
        final Action enemyAction;

        if (enemyActive.isFainted() || playerActive.isFainted()) {
            if (enemyActive.isFainted()) {
                //switch to next slot
                enemyAction = Action.getSwitch(this.enemy.getActiveSlot() + 1);
            } else {
                enemyAction = null;
            }
            
            if (playerActive.isFainted()) {
                int firstSwitch = -1;
                for (int i = 1; i <= 3; i++) {
                    final BattlingCreature ithSlot = this.player.getSlot(i);
                    if (ithSlot == null) break;
                    if (ithSlot.isFainted()) continue;

                    if (firstSwitch < 0) {
                        firstSwitch = i;
                    } else {
                        final OldBattleState branch = new OldBattleState(this);
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
                        final OldBattleState branch = new OldBattleState(this);
                        branch.queueActions(Action.getChargedAttack(i), enemyAction);
                        newBranches.add(branch);
                    } 
                }
                
                //always processes fast attack on current State, other actions are processed on new branches
                this.queueActions(Action.FAST_ATTACK, enemyAction);
            } else {
                this.queueActions(null, enemyAction);
            }
            
            this.timeElapsed += OldBattleState.TURN_TIME;
        }

        this.turnsElapsed++;
        this.comparisonKey++; //don't need any masking logic as the first section represents turnsElapsed

        return newBranches;
    }

    
    /**
     * for two states to be able to be comparable, they must both return the same comparisonKey
     * from this method. if they are comparable, then they can be compared using the isDominatedBy
     * method
     * 
     * the key is an int with its bits representing the following data:
     * - bits 0-11:  turnsElapsed (up to 4095) [cannot overflow because of timeElapsed limit]
     * - bits 12-13: playerActiveSlot (1-3)
     * - bits 14-15: enemyActiveSlot (1-3)
     * - bits 16-22: status for all 6 creatures (1 bit: active (0) or fainted/null (1))
     * @return
    */
    @Override
    public int getComparisonKey() {
       return this.comparisonKey;
    }
    
    private int getCreatureStatusCode(BattlingCreature c) {
        return (c == null || c.isFainted()) ? 1 : 0;
    }

    private int createComparisonKey() {
        int key = this.turnsElapsed;
        key |= (this.player.getActiveSlot() << 12);
        key |= (this.enemy.getActiveSlot() << 14);
        key |= (getCreatureStatusCode(this.player.getSlot(1)) << 16);
        key |= (getCreatureStatusCode(this.player.getSlot(2)) << 17);
        key |= (getCreatureStatusCode(this.player.getSlot(3)) << 18);
        key |= (getCreatureStatusCode(this.enemy.getSlot(1)) << 19);
        key |= (getCreatureStatusCode(this.enemy.getSlot(2)) << 20);
        key |= (getCreatureStatusCode(this.enemy.getSlot(3)) << 21);
        return key; 
    }

    /**
     * determines if this battle state is strictly dominated by another. a state is comparable if
     * it is not better in any aspect, and is worse in at least one aspect. time elapsed, shields, 
     * buffs, and creature hp and energy are all considered. to reduce overhead, this method does
     * not do any checks at runtime to see if states are comparable. to check that, compare these
     * states' comparison keys from the getComparisonKey method
     * @param other the state to compare against
     * @return true if this state is dominated by the other, false otherwise. NOTE: this 
     * implementation uses weak dominance, meaning a state is considered dominated if it is not 
     * strictly better than the other state in any aspect. this includes being equal, which allows
     * for pruning identical states
     */
    @Override
    public boolean isDominatedBy(BattleState otherBattle) {
        if (!(otherBattle instanceof OldBattleState other)) {
            return false;
        }
        if (this.timeElapsed < other.getTimeElapsed()) return false;

        if (this.player.getActive().getRemainingHp() > other.getPlayer().getActive().getRemainingHp()) return false;
        if (this.player.getActive().getEnergy() > other.getPlayer().getActive().getEnergy()) return false;
        if (this.enemy.getActive().getRemainingHp() < other.getEnemy().getActive().getRemainingHp()) return false;
        if (this.enemy.getActive().getEnergy() < other.getEnemy().getActive().getEnergy()) return false;

        if (this.player.getShields() > other.getPlayer().getShields()) return false;
        if (this.enemy.getShields() < other.getEnemy().getShields()) return false;
        if (this.player.getAtkBuff() > other.getPlayer().getAtkBuff()) return false;
        if (this.player.getDefBuff() > other.getPlayer().getDefBuff()) return false;
        if (this.enemy.getAtkBuff() < other.getEnemy().getAtkBuff()) return false;
        if (this.enemy.getDefBuff() < other.getEnemy().getDefBuff()) return false;
        
        if (this.enemy.getQueuedAction() == Action.STUN) {
            if (other.getEnemy().getQueuedAction() != Action.STUN) return false;
            if (this.enemy.getQueuedActionFulfills() > other.getEnemy().getQueuedActionFulfills()) return false;
        } else if (this.enemy.getQueuedAction() == Action.FAST_ATTACK && other.getEnemy().getQueuedAction() == Action.FAST_ATTACK) {
            if (this.enemy.getQueuedActionFulfills() > other.getEnemy().getQueuedActionFulfills()) return false;
        }
        return !(this.enemy.hasStunQueued() && !other.getEnemy().hasStunQueued());

        /*
        since enemies always use creatures front to back and simulations are currently only run
        with one creature, these are not currently necessary
        */
        // for (int i = 1; i <= 3; i++) {
        //     if (i == pActiveSlot) continue; // Already checked active
        //     BattlingCreature c1 = this.player.getSlot(i);
        //     if (c1 != null && !c1.isFainted()) {
        //         BattlingCreature c2 = other.player.getSlot(i);
        //         if (c1.getRemainingHp() > c2.getRemainingHp()) return false;
        //         if (c1.getEnergy() > c2.getEnergy()) return false;
        //     }
        // }
        // for (int i = 1; i <= 3; i++) {
        //     if (i == eActiveSlot) continue; // Already checked active
        //     BattlingCreature c1 = this.enemy.getSlot(i);
        //     if (c1 != null && !c1.isFainted()) {
        //         BattlingCreature c2 = other.enemy.getSlot(i);
        //         if (c1.getRemainingHp() < c2.getRemainingHp()) return false;
        //         if (c1.getEnergy() < c2.getEnergy()) return false;
        //     }
        // }

        /*
        commented out as fast attacks should always align (as switching mid battle is not allowed currently),
        making this check redundant. if things change, should be added back
        */
        // if (this.player.getQueuedAction() == Action.FAST_ATTACK && other.player.getQueuedAction() == Action.FAST_ATTACK) {
        //     if (this.player.getQueuedActionFulfills() < other.player.getQueuedActionFulfills()) return false;
        // }
    }

    /**
     * calculates how long it would take to win if each of the opponent's creatures was being
     * dealt the highest possible dps that the player's team can deal. the value returned by this
     * method will never exceed a solution of this state's timeElapsed.
     * @return the lower bound of the projection of timeElapsed
     */
    @Override
    public int getProjTimeElapsedLowerBound() {
        return this.timeElapsed
         + this.projTimeElapsedLowerBoundAddend
         + (int)((this.enemy.getActive().getRemainingHp() / this.maxDps[this.enemy.getActiveSlot()-1]) * 1000);
    }

    /**
     * calculates the maxDps array for a state. this array is used to get the projected timeElapsed lower bound
     * @param playerTeam the state's player's battling creatures
     * @param enemyTeam the state's enemy's battling creatures
     * @return an array where each value corresponds to the highest dps that the opponent's creature at the same
     * index can receive. for example, createMaxDpsArr()[2] is the maximum dps that any creature on playerTeam can 
     * deal to the creature at enemyTeam[2] 
     */
    private static double[] createMaxDpsArr(BattlingCreature[] playerTeam, BattlingCreature[] enemyTeam) {
        final double[] maxDpsArr = new double[enemyTeam.length];

        for (final BattlingCreature playerCreature : playerTeam) {
            if (playerCreature == null) break;

            boolean canPcBoostPcAttack = false;
            boolean canPcLowerEcDefense = false;

            final Move pcFastMove = playerCreature.getCreature().getFastMove();
            final List<Move> pcChargedMoves = playerCreature.getCreature().getChargedMoves();

            for (Move chargedMove : pcChargedMoves) {
                if (chargedMove.buffApplyChance() > OldBattleState.BUFF_APPLY_THRESHOLD) {
                    final var buffsSelf = chargedMove.buffsSelf();
                    final var buffsOpponent = chargedMove.buffsOpponent();
                    if (buffsSelf != null && buffsSelf.getAtk() > 0 && !canPcBoostPcAttack) {
                        canPcBoostPcAttack = true;
                    }
                    if (buffsOpponent != null && buffsOpponent.getDef() < 0 && !canPcLowerEcDefense) {
                        canPcLowerEcDefense = true;
                    }
                }
            }

            int i = 0;
            boolean canEcBoostPcAttack = false;
            for (BattlingCreature enemyCreature : enemyTeam) {
                if (enemyCreature == null) break;

                boolean canEcLowerEcDefense = false;
                
                final Move ecChargedMove = enemyCreature.getCreature().getChargedMoves().get(0);

                if (ecChargedMove.buffApplyChance() > OldBattleState.BUFF_APPLY_THRESHOLD) {
                    final var buffsOpponent = ecChargedMove.buffsOpponent();
                    final var buffsSelf = ecChargedMove.buffsSelf();
                    if (buffsOpponent != null && buffsOpponent.getAtk() > 0 && !canEcBoostPcAttack) {
                        canEcBoostPcAttack = true;
                    }
                    if (buffsSelf != null && buffsSelf.getDef() < 0 && !canEcLowerEcDefense) {
                        canEcLowerEcDefense = true;
                    }
                }

                double pcMaxDps = playerCreature.calculateDamageAgainst(
                    enemyCreature, 
                    pcFastMove, 
                    (canPcBoostPcAttack || canEcBoostPcAttack) ? BattlingCreature.MAX_BUFF_STAGES : 0, 
                    (canPcLowerEcDefense || canEcLowerEcDefense) ? -BattlingCreature.MAX_BUFF_STAGES : 0
                ) / (pcFastMove.turns() * 0.5);

                for (Move pcChargedMove : pcChargedMoves) {
                    final double pcCmDps = playerCreature.calculateDamageAgainst(
                        enemyCreature, 
                        pcChargedMove, 
                        (canPcBoostPcAttack || canEcBoostPcAttack) ? BattlingCreature.MAX_BUFF_STAGES : 0, 
                        (canPcLowerEcDefense || canEcLowerEcDefense) ? -BattlingCreature.MAX_BUFF_STAGES : 0
                    ) / (OldBattleState.PLAYER_CHARGE_TIME / 1000.0);

                    if (pcCmDps > pcMaxDps) pcMaxDps = pcCmDps;
                }

                if (pcMaxDps > maxDpsArr[i]) maxDpsArr[i] = pcMaxDps;
                i++;
            }
        }

        return maxDpsArr;
    }
    
    /**
     * calculates the lower bound of how long it takes to defeat the enemy's battling creatures that
     * are not active or already fainted. this number only changes when the opponent's active creature 
     * faints, so it's cached then to avoid extra operations when calculating the projected timeElapsed 
     * lower bound.
     * @return a projected timeElapsed lower bound addend
     */
    private int calculateProjTimeElapsedLowerBoundAddend() {
        int result = 0;
        //i starts at 1 above the index of the active battling creature. this is intentional as we are not interested 
        //in the active creature
        final BattlingCreature[] battlingCreatures = this.enemy.getBattlingCreatures();
        for (int i = this.enemy.getActiveSlot(); i < battlingCreatures.length; i++) {
            //remaining hp should be the same as the starting hp since these creatures haven't battled
            result += OldBattleState.FAINT_TIME + (int)((battlingCreatures[i].getRemainingHp() / this.maxDps[i-1]) * 1000);
        }

        return result;
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
