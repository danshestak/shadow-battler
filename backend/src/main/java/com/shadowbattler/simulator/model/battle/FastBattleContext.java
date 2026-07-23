package com.shadowbattler.simulator.model.battle;

import java.util.List;

import com.shadowbattler.simulator.model.Creature;
import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.model.Stats3;

public final class FastBattleContext {
    private final static float BONUS_MULTIPLIER = 1.3f;
    private final static float STAB_MULTIPLIER = 1.2f;
    private final static float SHADOW_ATK_MULTIPLIER = 6 / 5f;
    private final static float SHADOW_DEF_MULTIPLIER = 5 / 6f;
    private final static float[] BUFF_MULTIPLIERS = {
            4 / 8f, // -4
            4 / 7f, // -3
            4 / 6f, // -2
            4 / 5f, // -1
            1f,
            5 / 4f, // +1
            6 / 4f, // +2
            7 / 4f, // +3
            8 / 4f // +4
    };

    /** how long it takes for a turn/step to pass */
    public final static int TURN_TIME = 500;
    /** how long it takes to switch after having just switched */
    public final static int SWITCH_COOLDOWN = 45000;
    /** how long it takes for the player to charge their charged attack, including animations */
    public final static int PLAYER_CHARGE_TIME = 10000;
    /**
     * on paper, how long it takes for an enemy to charge their charged attack, but realistically this time
     * accounts for only how long animations take and how quickly a player can shield after the enemy starts
     * charging
     */
    public final static int ENEMY_CHARGE_TIME = 7000;
    /** how long it takes either trainer to switch to their next creature after their active faints */
    public final static int FAINT_TIME = 1000;
    /** how many turns the enemy is stunned for after a trainer switches/uses a charged move */
    public final static short STUN_TURNS = 8;

    public final static byte MAX_BUFF_STAGES = 4;
    public final static float BUFF_APPLY_THRESHOLD = 0.8f;

    public final short[] maxHp = new short[6];
    public final float[] atk = new float[6];
    // public final float[] def = new float[6]; //not currently necessary

    public final float[] fastDmg = new float[36]; // dmg matrices factor everything other than buffs and +1
    public final byte[] fastEnrg = new byte[6];
    public final byte[] fastTurns = new byte[6];

    public final float[] charged0Dmg = new float[36];
    public final byte[] charged0Enrg = new byte[6];
    public final byte[] charged0Buff = new byte[24]; // format: [userAtk, userDef, targetAtk, targetDef]

    public final float[] charged1Dmg = new float[36];
    public final byte[] charged1Enrg = new byte[6];
    public final byte[] charged1Buff = new byte[24];

    public final float[] maxPlayerDps = new float[3];

    public FastBattleContext(Creature[] player, Creature[] enemy) {
        for (int i = 0; i < 6; i++) {
            final Creature c = i < 3 ? player[i] : enemy[i - 3];
            if (c == null) continue;

            maxHp[i] = (short)(double)c.getStats().getHp();
            atk[i] = (float)(double)c.getStats().getAtk();
            // def[i] = (float)(double)c.getStats().getDef();

            final Move fast = c.getFastMove();
            fastEnrg[i] = (byte)fast.energyGain();
            fastTurns[i] = (byte)fast.turns();

            final Move charged0 = c.getChargedMoves().get(0);
            charged0Enrg[i] = (byte)(charged0.energy());
            if (charged0.buffApplyChance() > BUFF_APPLY_THRESHOLD) {
                final Stats3<Integer> userBuff = charged0.buffsSelf();
                if (userBuff != null) {
                    charged0Buff[i * 4] = (byte)(int)userBuff.getAtk();
                    charged0Buff[i * 4 + 1] = (byte)(int)userBuff.getDef();
                }
                final Stats3<Integer> targetBuff = charged0.buffsOpponent();
                if (targetBuff != null) {
                    charged0Buff[i * 4 + 2] = (byte)(int)targetBuff.getAtk();
                    charged0Buff[i * 4 + 3] = (byte)(int)targetBuff.getDef();
                }
            }

            if (c.getChargedMoves().size() > 1) {
                final Move charged1 = c.getChargedMoves().get(1);
                charged1Enrg[i] = (byte)(charged1.energy());
                if (charged1.buffApplyChance() > BUFF_APPLY_THRESHOLD) {
                    final Stats3<Integer> userBuff = charged1.buffsSelf();
                    if (userBuff != null) {
                        charged1Buff[i * 4] = (byte)(int)userBuff.getAtk();
                        charged1Buff[i * 4 + 1] = (byte)(int)userBuff.getDef();
                    }
                    final Stats3<Integer> targetBuff = charged1.buffsOpponent();
                    if (targetBuff != null) {
                        charged1Buff[i * 4 + 2] = (byte)(int)targetBuff.getAtk();
                        charged1Buff[i * 4 + 3] = (byte)(int)targetBuff.getDef();
                    }
                }
            } else {
                charged1Enrg[i] = Byte.MAX_VALUE; //move is unreachable since energy is capped at 100
            }
        }
        
        for (int userIndex = 0; userIndex < 6; userIndex++) {
            final Creature user = userIndex < 3 ? player[userIndex] : enemy[userIndex - 3];
            if (user == null) continue;

            final float effectiveUserAtk = (float)(double)user.getStats().getAtk() 
                * 0.5f
                * BONUS_MULTIPLIER
                * (user.getSpecies().isShadow() ? SHADOW_ATK_MULTIPLIER : 1);
            
            final Move fast = user.getFastMove();
            final Move charged0 = user.getChargedMoves().get(0);
            final Move charged1 = user.getChargedMoves().size() > 1 ? user.getChargedMoves().get(1) : null;

            for (int targetIndex = 0; targetIndex < 6; targetIndex++) {
                //no need to calculate teammates attacking each other
                if ((userIndex < 3 && targetIndex < 3) || (userIndex >= 3 && targetIndex >= 3)) continue;

                final Creature target = targetIndex < 3 ? player[targetIndex] : enemy[targetIndex - 3];
                if (target == null) continue;

                final float effectiveTargetDef = (float)(double)target.getStats().getDef()
                    * (target.getSpecies().isShadow() ? SHADOW_DEF_MULTIPLIER : 1);
                
                final int matrixIndex = userIndex * 6 + targetIndex;

                fastDmg[matrixIndex] = (float)(fast.power()
                    * effectiveUserAtk
                    * (user.getSpecies().givesStabTo(fast) ? STAB_MULTIPLIER : 1f)
                    * fast.type().effectivenessAgainst(target.getSpecies().getTypes())
                    / effectiveTargetDef);

                charged0Dmg[matrixIndex] = (float)(charged0.power()
                    * effectiveUserAtk
                    * (user.getSpecies().givesStabTo(charged0) ? STAB_MULTIPLIER : 1f)
                    * charged0.type().effectivenessAgainst(target.getSpecies().getTypes())
                    / effectiveTargetDef);

                if (charged1 != null) {
                    charged1Dmg[matrixIndex] = (float)(charged1.power()
                        * effectiveUserAtk
                        * (user.getSpecies().givesStabTo(charged1) ? STAB_MULTIPLIER : 1f)
                        * charged1.type().effectivenessAgainst(target.getSpecies().getTypes())
                        / effectiveTargetDef);
                }
            }
        }

        for (int playerIndex = 0; playerIndex < 3; playerIndex++) {
            final Creature playerCreature = player[playerIndex];
            if (playerCreature == null) break;

            boolean canPcBoostPcAttack = false;
            boolean canPcLowerEcDefense = false;

            final Move pcFastMove = playerCreature.getFastMove();
            final List<Move> pcChargedMoves = playerCreature.getChargedMoves();

            for (Move chargedMove : pcChargedMoves) {
                if (chargedMove.buffApplyChance() > BUFF_APPLY_THRESHOLD) {
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

            boolean canEcBoostPcAttack = false;
            for (int enemyIndex = 0; enemyIndex < 3; enemyIndex++) {
                final Creature enemyCreature = enemy[enemyIndex];                
                if (enemyCreature == null) break;

                boolean canEcLowerEcDefense = false;
                
                final Move ecChargedMove = enemyCreature.getChargedMoves().get(0);

                if (ecChargedMove.buffApplyChance() > BUFF_APPLY_THRESHOLD) {
                    final var buffsOpponent = ecChargedMove.buffsOpponent();
                    final var buffsSelf = ecChargedMove.buffsSelf();
                    if (buffsOpponent != null && buffsOpponent.getAtk() > 0 && !canEcBoostPcAttack) {
                        canEcBoostPcAttack = true;
                    }
                    if (buffsSelf != null && buffsSelf.getDef() < 0 && !canEcLowerEcDefense) {
                        canEcLowerEcDefense = true;
                    }
                }

                final float fastMaxDps = getFastDmgWithBuff(
                    playerIndex, 
                    enemyIndex + 3, 
                    (canPcBoostPcAttack || canEcBoostPcAttack) ? MAX_BUFF_STAGES : 0, 
                    (canPcLowerEcDefense || canEcLowerEcDefense) ? -MAX_BUFF_STAGES : 0
                ) / (pcFastMove.turns() * TURN_TIME/1000f);

                final float charged0MaxDps = getCharged0DmgWithBuff(
                    playerIndex, 
                    enemyIndex + 3, 
                    (canPcBoostPcAttack || canEcBoostPcAttack) ? MAX_BUFF_STAGES : 0, 
                    (canPcLowerEcDefense || canEcLowerEcDefense) ? -MAX_BUFF_STAGES : 0
                ) / (PLAYER_CHARGE_TIME / 1000f);

                float charged1MaxDps = 0;
                if (pcChargedMoves.size() > 1) {
                    charged1MaxDps = getCharged1DmgWithBuff(
                        playerIndex, 
                        enemyIndex + 3, 
                        (canPcBoostPcAttack || canEcBoostPcAttack) ? MAX_BUFF_STAGES : 0, 
                        (canPcLowerEcDefense || canEcLowerEcDefense) ? -MAX_BUFF_STAGES : 0
                    ) / (PLAYER_CHARGE_TIME / 1000f);
                }

                float maxDps = fastMaxDps > charged0MaxDps ? fastMaxDps : charged0MaxDps;
                if (charged1MaxDps > maxDps) maxDps = charged1MaxDps;
                
                if (maxDps > this.maxPlayerDps[enemyIndex]) this.maxPlayerDps[enemyIndex] = maxDps;
            }
        }
    }

    public short getFastDmgWithBuff(int userIndex, int targetIndex, int userAtkBuff, int targetDefBuff) {
        return (short) (1 + fastDmg[userIndex * 6 + targetIndex]
                * BUFF_MULTIPLIERS[userAtkBuff + MAX_BUFF_STAGES]
                * BUFF_MULTIPLIERS[MAX_BUFF_STAGES - targetDefBuff]);
    }

    public short getCharged0DmgWithBuff(int userIndex, int targetIndex, int userAtkBuff, int targetDefBuff) {
        return (short) (1 + charged0Dmg[userIndex * 6 + targetIndex]
                * BUFF_MULTIPLIERS[userAtkBuff + MAX_BUFF_STAGES]
                * BUFF_MULTIPLIERS[MAX_BUFF_STAGES - targetDefBuff]);
    }

    public short getCharged1DmgWithBuff(int userIndex, int targetIndex, int userAtkBuff, int targetDefBuff) {
        return (short) (1 + charged1Dmg[userIndex * 6 + targetIndex]
                * BUFF_MULTIPLIERS[userAtkBuff + MAX_BUFF_STAGES]
                * BUFF_MULTIPLIERS[MAX_BUFF_STAGES - targetDefBuff]);
    }
}