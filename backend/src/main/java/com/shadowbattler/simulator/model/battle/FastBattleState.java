package com.shadowbattler.simulator.model.battle;

import java.util.ArrayList;
import java.util.List;

import com.shadowbattler.simulator.model.Creature;
import com.shadowbattler.simulator.model.Team;

public final class FastBattleState {
    public final FastBattleContext context;
    public final BattleLog log;

    public short turnsElapsed;
    public int timeElapsed;
    public boolean finished;
    
    //using longs instead of arrays to reduce object allocations
    public long playerHpLong;
    public long enemyHpLong;
    public long energyLong;
    
    public byte playerActiveIndex;
    public byte enemyActiveIndex;
    public byte playerShields;
    public byte enemyShields;
    public byte playerRemainingCreatures;
    public byte enemyRemainingCreatures;
    public byte playerAtkBuff;
    public byte playerDefBuff;
    public byte enemyAtkBuff;
    public byte enemyDefBuff;
    public byte playerQueuedAction;
    public byte enemyQueuedAction;
    public short playerQueuedActionFulfills;
    public short enemyQueuedActionFulfills;
    public int playerSwitchCooldownEnds;
    public boolean enemyStunQueued;
    
    public int comparisonKey;
    public int projTimeElapsedLowerBoundAddend;

    public FastBattleState(Team<Creature> playerTeam, Team<Creature> enemyTeam, byte enemyStartingShields,
            boolean isLogged) {
        final Creature[] playerArr = new Creature[] {
                playerTeam.getFirst(),
                playerTeam.getSecond(),
                playerTeam.getThird()
        };
        final Creature[] enemyArr = new Creature[] {
                enemyTeam.getFirst(),
                enemyTeam.getSecond(),
                enemyTeam.getThird()
        };

        this.context = new FastBattleContext(playerArr, enemyArr);
        this.log = isLogged ? new BattleLog() : null;

        this.turnsElapsed = 0;
        this.timeElapsed = 0;
        this.finished = false;

        for (int i = 0; i < this.context.maxHp.length; i++) {
            this.setHp(i, this.context.maxHp[i]);
        }

        this.energyLong = 0;
        
        this.playerActiveIndex = 0;
        this.enemyActiveIndex = 3;
        this.playerShields = 2;
        this.enemyShields = enemyStartingShields;
        this.playerRemainingCreatures = (byte) playerTeam.size();
        this.enemyRemainingCreatures = (byte) enemyTeam.size();
        this.playerAtkBuff = 0;
        this.playerDefBuff = 0;
        this.enemyAtkBuff = 0;
        this.enemyDefBuff = 0;
        this.playerQueuedAction = Actions.NONE;
        this.enemyQueuedAction = Actions.NONE;
        this.playerQueuedActionFulfills = 0;
        this.enemyQueuedActionFulfills = 0;
        this.playerSwitchCooldownEnds = 0;
        this.enemyStunQueued = false;

        this.projTimeElapsedLowerBoundAddend = this.calculateProjTimeElapsedLowerBoundAddend();
        this.comparisonKey = this.createComparisonKey();
    }

    public FastBattleState(FastBattleState other) {
        this.context = other.context;
        this.log = other.log;

        this.turnsElapsed = other.turnsElapsed;
        this.timeElapsed = other.timeElapsed;
        this.finished = other.finished;

        this.playerHpLong = other.playerHpLong;
        this.enemyHpLong = other.enemyHpLong;
        this.energyLong = other.energyLong;
        
        this.playerActiveIndex = other.playerActiveIndex;
        this.enemyActiveIndex = other.enemyActiveIndex;
        this.playerShields = other.playerShields;
        this.enemyShields = other.enemyShields;
        this.playerRemainingCreatures = other.playerRemainingCreatures;
        this.enemyRemainingCreatures = other.enemyRemainingCreatures;
        this.playerAtkBuff = other.playerAtkBuff;
        this.playerDefBuff = other.playerDefBuff;
        this.enemyAtkBuff = other.enemyAtkBuff;
        this.enemyDefBuff = other.enemyDefBuff;
        this.playerQueuedAction = other.playerQueuedAction;
        this.enemyQueuedAction = other.enemyQueuedAction;
        this.playerQueuedActionFulfills = other.playerQueuedActionFulfills;
        this.enemyQueuedActionFulfills = other.enemyQueuedActionFulfills;
        this.playerSwitchCooldownEnds = other.playerSwitchCooldownEnds;
        this.enemyStunQueued = other.enemyStunQueued;

        this.projTimeElapsedLowerBoundAddend = other.projTimeElapsedLowerBoundAddend;
        this.comparisonKey = other.comparisonKey;
    }

    public byte getEnergy(int index) {
        return (byte) ((this.energyLong >>> (index << 3)) & 0xFF);
    }

    public void setEnergy(int index, byte value) {
        final int shift = index << 3;
        this.energyLong = (this.energyLong & ~(0xFFL << shift)) | ((value & 0xFFL) << shift);
    }

    public short getHp(int index) {
        if (index < 3) {
            return (short)((this.playerHpLong >>> (index << 4)) & 0xFFFF);
        } else {
            return (short)((this.enemyHpLong >>> ((index - 3) << 4)) & 0xFFFF);
        }
    }

    public void setHp(int index, short value) {
        if (index < 3) {
            final int shift = index << 4;
            this.playerHpLong = (this.playerHpLong & ~(0xFFFFL << shift)) | ((value & 0xFFFFL) << shift);
        } else {
            final int shift = (index - 3) << 4;
            this.enemyHpLong = (this.enemyHpLong & ~(0xFFFFL << shift)) | ((value & 0xFFFFL) << shift);
        }
    }

    /**
     * sets both trainer's queuedAction to their respective action argument, and
     * sets their queuedActionFulfills
     * 
     * @param playerAction the action for this state's player to queue
     * @param enemyAction  the action for this state's enemy to queue
     */
    private void queueActions(byte playerAction, byte enemyAction) {
        if (playerAction != Actions.NONE) {
            this.playerQueuedAction = playerAction;
            this.playerQueuedActionFulfills = (short)(this.turnsElapsed + switch (playerAction) {
                case Actions.FAST_ATTACK -> this.context.fastTurns[this.playerActiveIndex];
                case Actions.STUN -> FastBattleContext.STUN_TURNS - 1;
                default -> 1;
            });
        }
        if (enemyAction != Actions.NONE) {
            this.enemyQueuedAction = enemyAction;
            this.enemyQueuedActionFulfills = (short)(this.turnsElapsed + switch (enemyAction) {
                case Actions.FAST_ATTACK -> this.context.fastTurns[this.enemyActiveIndex];
                case Actions.STUN -> FastBattleContext.STUN_TURNS - 1;
                default -> 1;
            });
        }
    }

    /**
     * consumes and processes the user's queued action. should not be used outside of 
     * processQueuedActions, as that method considers ties and priority, and handles fainting
     * 
     * @param user     the trainer who will use their queued action
     * @param isPlayer true if user is the player, false if user is the enemy
     */
    private void processQueuedAction(boolean isPlayer) {
        final byte queuedAction = isPlayer ? this.playerQueuedAction : this.enemyQueuedAction;
        if (queuedAction == Actions.NONE) return;
        final byte userActiveIndex = isPlayer ? this.playerActiveIndex : this.enemyActiveIndex;
        final byte targetActiveIndex = isPlayer ? this.enemyActiveIndex : this.playerActiveIndex;

        final short targetStartingHp = this.getHp(targetActiveIndex);

        if (queuedAction == Actions.FAST_ATTACK) {
            final short dmg = this.context.getFastDmgWithBuff(
                userActiveIndex,
                targetActiveIndex,
                isPlayer ? this.playerAtkBuff : this.enemyAtkBuff,
                isPlayer ? this.enemyDefBuff : this.playerDefBuff
            );

            final short currHp = this.getHp(targetActiveIndex);
            if (dmg > currHp) {
                this.setHp(targetActiveIndex, (short)0);
            } else {
                this.setHp(targetActiveIndex, (short)(currHp - dmg));
            }

            final byte currEnergy = getEnergy(userActiveIndex);
            setEnergy(userActiveIndex, (byte) Math.min(
                currEnergy + this.context.fastEnrg[userActiveIndex], 
                100
            ));
        } else if (Actions.isChargedAttack(queuedAction)) {
            /*
             * the enemy ai always shields if they have them available, and for the player
             * there
             * are very few cases where not shielding is worth it. to reduce branches, the
             * player will also always shield when available
             */
            boolean targetFainted = false;
            if ((isPlayer ? this.enemyShields : this.playerShields) > 0) {
                // TODO: add shield damage
                if (isPlayer) {
                    this.enemyShields--;
                } else {
                    this.playerShields--;
                }
            } else {
                final short dmg;
                if (queuedAction == Actions.CHARGED_ATTACK0) {
                    dmg = this.context.getCharged0DmgWithBuff(
                        userActiveIndex,
                        targetActiveIndex,
                        isPlayer ? this.playerAtkBuff : this.enemyAtkBuff,
                        isPlayer ? this.enemyDefBuff : this.playerDefBuff
                    );
                } else {
                    dmg = this.context.getCharged1DmgWithBuff(
                        userActiveIndex,
                        targetActiveIndex,
                        isPlayer ? this.playerAtkBuff : this.enemyAtkBuff,
                        isPlayer ? this.enemyDefBuff : this.playerDefBuff
                    );
                }

                final short currHp = this.getHp(targetActiveIndex);
                if (dmg > currHp) {
                    this.setHp(targetActiveIndex, (short)0);
                    targetFainted = true;
                } else {
                    this.setHp(targetActiveIndex, (short)(currHp - dmg));
                }
            }

            final byte currEnergy = getEnergy(userActiveIndex);
            final byte buffs[];
            if (queuedAction == Actions.CHARGED_ATTACK0) {
                setEnergy(userActiveIndex, (byte)(currEnergy - this.context.charged0Enrg[userActiveIndex]));
                buffs = this.context.charged0Buff;
            } else { // CHARGED_ATTACK1
                setEnergy(userActiveIndex, (byte)(currEnergy - this.context.charged1Enrg[userActiveIndex]));
                buffs = this.context.charged1Buff;
            }

            final int bIdx = userActiveIndex * 4;
            if (buffs[bIdx] != 0 || buffs[bIdx + 1] != 0 || buffs[bIdx + 2] != 0 || buffs[bIdx + 3] != 0) {
                if (isPlayer) {
                    if (buffs[bIdx] != 0) this.playerAtkBuff = clampBuff(this.playerAtkBuff, buffs[bIdx]);
                    if (buffs[bIdx+1] != 0) this.playerDefBuff = clampBuff(this.playerDefBuff, buffs[bIdx+1]);
                    if (buffs[bIdx+2] != 0) this.enemyAtkBuff  = clampBuff(this.enemyAtkBuff, buffs[bIdx+2]);
                    if (buffs[bIdx+3] != 0) this.enemyDefBuff  = clampBuff(this.enemyDefBuff, buffs[bIdx+3]);
                } else {
                    if (buffs[bIdx] != 0) this.enemyAtkBuff  = clampBuff(this.enemyAtkBuff, buffs[bIdx]);
                    if (buffs[bIdx+1] != 0) this.enemyDefBuff  = clampBuff(this.enemyDefBuff, buffs[bIdx+1]);
                    if (buffs[bIdx+2] != 0) this.playerAtkBuff = clampBuff(this.playerAtkBuff, buffs[bIdx+2]);
                    if (buffs[bIdx+3] != 0) this.playerDefBuff = clampBuff(this.playerDefBuff, buffs[bIdx+3]);
                }
            }

            // apply floating fast moves
            if (!targetFainted && (isPlayer ? this.enemyQueuedActionFulfills : this.playerQueuedActionFulfills) <= this.turnsElapsed) {
                this.processQueuedAction(!isPlayer);
            }

            // account for charge time
            this.timeElapsed += (isPlayer ? FastBattleContext.PLAYER_CHARGE_TIME : FastBattleContext.ENEMY_CHARGE_TIME);

            this.enemyStunQueued = true;
        } else if (Actions.isSwitch(queuedAction)) {
            // forced switch after fainting has no effect on cooldown
            if (this.getHp(userActiveIndex) > 0) {
                if (isPlayer) {
                    this.playerSwitchCooldownEnds = this.timeElapsed + FastBattleContext.SWITCH_COOLDOWN;
                } else {
                    throw new RuntimeException("enemy tried to switch before fainting");
                }
            }

            if (isPlayer) {
                this.playerActiveIndex = (byte)Actions.getId(queuedAction);
                this.playerAtkBuff = 0;
                this.playerDefBuff = 0;

                this.comparisonKey &= ~(0x3 << 12);
                this.comparisonKey |= (this.playerActiveIndex << 12);
            } else {
                this.enemyActiveIndex = (byte)(Actions.getId(queuedAction) + 3);
                this.enemyAtkBuff = 0;
                this.enemyDefBuff = 0;
                
                this.comparisonKey &= ~(0x3 << 14);
                this.comparisonKey |= ((this.enemyActiveIndex - 3) << 14);

                //projTimeElapsedLowerBoundAddend changes when enemy's active changes
                this.projTimeElapsedLowerBoundAddend = this.calculateProjTimeElapsedLowerBoundAddend();
            }

            this.enemyStunQueued = true;
        } else if (queuedAction == Actions.STUN) {
            this.enemyStunQueued = false;
        }

        if (this.log != null && queuedAction != Actions.NONE) {
            // this.log.addEntry(
            //     this,
            //     user,
            //     user.getQueuedAction(),
            //     beforeHp,
            //     opp.getActive().getRemainingHp()
            // );
        }

        if (isPlayer) {
            this.playerQueuedAction = Actions.NONE;
            this.playerQueuedActionFulfills = 0;
        } else {
            this.enemyQueuedAction = Actions.NONE;
            this.enemyQueuedActionFulfills = 0;
        }
    }

    /**
     * consumes and processes both of the state's trainers' actions through processQueuedAction,
     * resolves ties and move priority, handles trainer's remaining creature count after one faints,
     * and marks the state as finished if a trainer runs out of creatures
     */
    private void processQueuedActions() {
        final byte playerFulfilledAction = this.playerQueuedActionFulfills <= this.turnsElapsed ? this.playerQueuedAction : Actions.NONE;
        final byte enemyFulfilledAction = this.enemyQueuedActionFulfills <= this.turnsElapsed ? this.enemyQueuedAction : Actions.NONE;

        /*
        fainted creatures are forced to switch with no other action being available that turn, so assume both
        fulfilled actions are a switch or null and there is at least one switch if either creature is fainted
        */
        if (this.getHp(this.playerActiveIndex) <= 0 || this.getHp(this.enemyActiveIndex) <= 0) {
            //account for time needed to switch. an OR statement to avoid double counting if both trainers switch
            this.timeElapsed += FastBattleContext.FAINT_TIME;
        }

        if (playerFulfilledAction != Actions.NONE && enemyFulfilledAction != Actions.NONE) {
            boolean playerPriority = false;
            boolean considerPriority = true;

            if (Actions.isSwitch(playerFulfilledAction) ^ Actions.isSwitch(enemyFulfilledAction)) {
                //one trainer is switching and the other isnt
                //switching has priority over every other action
                playerPriority = Actions.isSwitch(playerFulfilledAction);
            } else if (Actions.isChargedAttack(playerFulfilledAction) || Actions.isChargedAttack(enemyFulfilledAction)) {
                if (Actions.isChargedAttack(playerFulfilledAction) && Actions.isChargedAttack(enemyFulfilledAction)) {
                    //cmp tie
                    //assuming pve battles are like pvp, cmp ties use creature attack rather than effective attack
                    //treating equal atk as cmp tie loss for player to avoid randomness
                    playerPriority = this.context.atk[playerActiveIndex] > this.context.atk[enemyActiveIndex];
                } else {
                    //pvpoke gives priority to a fast attack thats lethal against a charged attack user. afaik, this
                    //logic is no longer in the game so i have not implemented it
                    playerPriority = Actions.isChargedAttack(playerFulfilledAction);
                }
            } else {
                considerPriority = false;
            }

            if (considerPriority) {
                this.processQueuedAction(playerPriority);
                if (this.getHp(playerPriority ? this.enemyActiveIndex : this.playerActiveIndex) > 0) {
                    this.processQueuedAction(!playerPriority);
                }
            } else {
                this.processQueuedAction(true);
                this.processQueuedAction(false);
            }
        } else if (playerFulfilledAction != Actions.NONE || enemyFulfilledAction != Actions.NONE) {
            this.processQueuedAction(playerFulfilledAction != Actions.NONE);
        }

        if (playerFulfilledAction != Actions.NONE || enemyFulfilledAction != Actions.NONE) {
            this.checkTrainerFaint(true);
            this.checkTrainerFaint(false);
        }

        //end battle after 12 minutes
        if (this.timeElapsed > 720000) {
            this.finished = true;
        }
    }

    private void checkTrainerFaint(boolean isPlayer) {
        if (this.getHp(isPlayer ? this.playerActiveIndex : this.enemyActiveIndex) > 0) return;

        final byte newRemainingCreatures;
        if (isPlayer) {
            newRemainingCreatures = --this.playerRemainingCreatures;
            this.playerQueuedAction = Actions.NONE;
            this.playerQueuedActionFulfills = 0;
        } else {
            newRemainingCreatures = --this.enemyRemainingCreatures;
            this.enemyQueuedAction = Actions.NONE;
            this.enemyQueuedActionFulfills = 0;
        }
        
        this.comparisonKey |= (1 << ((isPlayer ? this.playerActiveIndex : this.enemyActiveIndex) + 16));
        if (newRemainingCreatures <= 0) {
            this.finished = true;
        }
    }
    
    public boolean playerWon() {
        return this.playerRemainingCreatures > 0 && this.enemyRemainingCreatures <= 0;
    }

    /**
     * handles all logic for a single turn in a battle. first processes queued actions, decides which actions 
     * each trainer will take, creates branching states for each decision the player can make, and iterates 
     * the stepElapsed and timeElapsed
     * @return a list of new states created from branching player decisions. the current state is also advanced
     */
    public List<FastBattleState> step() {
        this.processQueuedActions();
        if (this.finished) return new ArrayList<>();

        final List<FastBattleState> newBranches = new ArrayList<>();

        final byte enemyAction;

        if (this.getHp(this.playerActiveIndex) <= 0 || this.getHp(this.enemyActiveIndex) <= 0) {
            if (this.getHp(this.enemyActiveIndex) <= 0) {
                //switch to next slot
                enemyAction = Actions.getSwitch(this.enemyActiveIndex - 3 + 1);
            } else {
                enemyAction = Actions.NONE;
            }
            
            if (this.getHp(this.playerActiveIndex) <= 0) {
                byte firstSwitch = -1;
                for (byte i = 0; i < 3; i++) {
                    if (i == this.playerActiveIndex || this.getHp(i) <= 0) continue;

                    if (firstSwitch < 0) {
                        firstSwitch = i;
                    } else {
                        final FastBattleState branch = new FastBattleState(this);
                        branch.queueActions(Actions.getSwitch(i), enemyAction);
                        newBranches.add(branch);
                    }
                }
                //current State handles first possible switch, other is on another branch
                this.queueActions(Actions.getSwitch(firstSwitch), enemyAction);
            } else {
                this.queueActions(Actions.NONE, enemyAction);
            }

            //turns are not elapsed when switching due to a creature fainting
        } else {
            if (this.enemyQueuedAction == Actions.NONE) {
                if (this.getEnergy(this.enemyActiveIndex) >= this.context.charged0Enrg[this.enemyActiveIndex]) {
                    //always uses charged move when available
                    enemyAction = Actions.CHARGED_ATTACK0;
                } else if (this.enemyStunQueued) {
                    enemyAction = Actions.STUN;
                    this.enemyStunQueued = false;
                } else {
                    enemyAction = Actions.FAST_ATTACK;
                }
            } else {
                /*
                if the enemy hasStunQueued set while they are already stunned, restart the stun duration
                now instead of stunning them again after their current stun fulfills
                */
                if (this.enemyStunQueued && this.enemyQueuedAction == Actions.STUN) {
                    enemyAction = Actions.STUN;
                    this.enemyStunQueued = false;
                } else {
                    enemyAction = Actions.NONE;
                }
            }

            if (this.playerQueuedAction == Actions.NONE) {
                if (this.getEnergy(this.playerActiveIndex) >= this.context.charged0Enrg[this.playerActiveIndex]) {
                    final FastBattleState branch = new FastBattleState(this);
                    branch.queueActions(Actions.CHARGED_ATTACK0, enemyAction);
                    newBranches.add(branch);
                }

                if (this.getEnergy(this.playerActiveIndex) >= this.context.charged1Enrg[this.playerActiveIndex]) {
                    //if the player does not have a second charged attack, then
                    //this.context.charged1Enrg[this.playerActiveIndex] always evaluates to false
                    final FastBattleState branch = new FastBattleState(this);
                    branch.queueActions(Actions.CHARGED_ATTACK1, enemyAction);
                    newBranches.add(branch);
                }
                
                //always processes fast attack on current State, other actions are processed on new branches
                this.queueActions(Actions.FAST_ATTACK, enemyAction);
            } else {
                this.queueActions(Actions.NONE, enemyAction);
            }
            
            this.timeElapsed += FastBattleContext.TURN_TIME;
        }

        this.turnsElapsed++;
        this.comparisonKey++; //don't need any masking logic as the first section represents turnsElapsed

        return newBranches;
    }

    private static byte clampBuff(int current, byte delta) {
        return (byte)Math.clamp(current + delta, -FastBattleContext.MAX_BUFF_STAGES, FastBattleContext.MAX_BUFF_STAGES);
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
    public int getComparisonKey() {
       return this.comparisonKey;
    }
    
    private int createComparisonKey() {
        int key = this.turnsElapsed;
        key |= this.playerActiveIndex << 12;
        key |= (this.enemyActiveIndex - 3) << 14;
        for (byte i = 0; i < 6; i++) {
            key |= (this.getHp(i) <= 0 ? 1 : 0) << (16 + i);
        }
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
    public boolean isDominatedBy(FastBattleState other) {
        if (this.timeElapsed < other.timeElapsed) return false;

        if (this.getHp(this.playerActiveIndex) > other.getHp(this.playerActiveIndex)) return false;
        if (this.getHp(this.enemyActiveIndex) < other.getHp(this.enemyActiveIndex)) return false;
        if (this.getEnergy(this.playerActiveIndex) > other.getEnergy(this.playerActiveIndex)) return false;
        if (this.getEnergy(this.enemyActiveIndex) < other.getEnergy(this.enemyActiveIndex)) return false;

        if (this.playerShields > other.playerShields) return false;
        if (this.enemyShields < other.enemyShields) return false;
        if (this.playerAtkBuff > other.playerAtkBuff) return false;
        if (this.playerDefBuff > other.playerDefBuff) return false;
        if (this.enemyAtkBuff < other.enemyAtkBuff) return false;
        if (this.enemyDefBuff < other.enemyDefBuff) return false;
        
        if (this.enemyQueuedAction == Actions.STUN) {
            if (other.enemyQueuedAction != Actions.STUN) return false;
            if (this.enemyQueuedActionFulfills > other.enemyQueuedActionFulfills) return false;
        } else if (this.enemyQueuedAction == Actions.FAST_ATTACK && other.enemyQueuedAction == Actions.FAST_ATTACK) {
            if (this.enemyQueuedActionFulfills > other.enemyQueuedActionFulfills) return false;
        }
        return !(this.enemyStunQueued && !other.enemyStunQueued);
    }

    /**
     * calculates how long it would take to win if each of the opponent's creatures was being
     * dealt the highest possible dps that the player's team can deal. the value returned by this
     * method will never exceed a solution of this state's timeElapsed.
     * @return the lower bound of the projection of timeElapsed
     */
    public int getProjTimeElapsedLowerBound() {
        return this.timeElapsed
            + this.projTimeElapsedLowerBoundAddend
            + (int)((this.getHp(this.enemyActiveIndex) / this.context.maxPlayerDps[this.enemyActiveIndex-3]) * 1000);
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
        for (int i = this.enemyActiveIndex+1; i < 6; i++) {
            //remaining hp should be the same as the starting hp since these creatures haven't battled
            result += FastBattleContext.FAINT_TIME;
            result += (int)((this.getHp(i) / this.context.maxPlayerDps[i-3]) * 1000);
        }

        return result;
    }
}
