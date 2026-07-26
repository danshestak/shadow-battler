package com.shadowbattler.simulator.model.battle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.shadowbattler.simulator.model.Creature;
import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.model.Opponent;
import com.shadowbattler.simulator.model.Species;
import com.shadowbattler.simulator.model.Stats3;
import com.shadowbattler.simulator.model.Team;

public class OpponentBattleSolver implements BattleSolver {
    private BattleResult battleResult = null;
    private final Team<Creature> playerTeam;
    private final Opponent opponent;
    private final int trainerLevel;

    /**
     * constructor for OpponentBattleSolver when the opponent is a rocket member
     * @param playerTeam team of creatures that the player uses
     * @param opponent the opponent the player is battling. should be a rocket member
     * @param trainerLevel the trainer level. affects the stats of rocket opponents' creatures
     */
    public OpponentBattleSolver(Team<Creature> playerTeam, Opponent opponent, int trainerLevel) {
        this.playerTeam = playerTeam;
        this.opponent = opponent;
        this.trainerLevel = trainerLevel;
    }

    @Override
    public void solve() {
        final int lineupCombinationQty = opponent.getLineupSpecies().combinationQuantity();
        final List<Team<Creature>> teams = new ArrayList<>();

        for (int lineupId = 0; lineupId < lineupCombinationQty; lineupId++) {
            final Team<Species> lineup = opponent.getLineupSpecies().combinationFromId(lineupId);

            final List<Creature> firstSlotCreatures = getCreaturesForSlot(lineup.getFirst());
            final List<Creature> secondSlotCreatures = getCreaturesForSlot(lineup.getSecond());
            final List<Creature> thirdSlotCreatures = getCreaturesForSlot(lineup.getThird());

            for (Creature first : firstSlotCreatures) {
                for (Creature second : secondSlotCreatures) {
                    for (Creature third : thirdSlotCreatures) {
                        teams.add(new Team<>(first, second, third));
                    }
                }
            }
        }

        final Map<String, BattleResult> sharedResults = new HashMap<>();

        final List<BattleResult> battleResults = teams.stream()
            .map(t -> {
                final TeamBattleSolver teamBattleSolver = new TeamBattleSolver(
                    this.playerTeam,
                    t,
                    opponent.getTitle().getShields()
                );
                final BattleContext context = teamBattleSolver.getBattleState().context;

                boolean playerAtkDrop = (context.charged0Buff[0] < 0) || (context.charged1Buff[0] < 0);
                final boolean[] enemyCanReachCharged = new boolean[]{true, true, true};
                for (int i = 0; i < 3; i++) {
                    final short playerfastDmg = context.getFastDmgWithBuff(0, 3+i, playerAtkDrop ? -BattleContext.MAX_BUFF_STAGES : 0, 0);
                    final int fastAtksToKo = (int)Math.ceil((double)context.maxHp[i] / playerfastDmg);
                    final int turns = context.fastTurns[0] * fastAtksToKo;
                    final int enemyMinEnergy = (turns / context.fastTurns[3+i]) * context.fastEnrg[3+i];
                    if (enemyMinEnergy < context.charged0Enrg[3+i]) {
                        enemyCanReachCharged[i] = false;
                    } else {
                        if (!playerAtkDrop) {
                            playerAtkDrop = (context.charged0Buff[4 * (3+i) + 2] < 0) || (context.charged1Buff[4 * (3+i) + 2] < 0);
                        }
                    }
                }

                if (enemyCanReachCharged[0] && enemyCanReachCharged[1] && enemyCanReachCharged[2]) {
                    teamBattleSolver.solve();
                    return teamBattleSolver.getBattleResult();
                } else {
                    final String key = OpponentBattleSolver.createEnemyTeamKey(t, enemyCanReachCharged);
                    final BattleResult shared = sharedResults.get(key);
                    if (shared != null) {
                        return shared;
                    } else {
                        teamBattleSolver.solve();
                        final BattleResult br = teamBattleSolver.getBattleResult();
                        sharedResults.put(key, br);
                        return br;
                    }
                }
            })
            .toList();

        this.battleResult = BattleResult.averageOf(battleResults);
    }
    
    private List<Creature> getCreaturesForSlot(Species species) {
        if (species == null) {
            return List.of((Creature) null);
        }

        final int combinationsQuantity = species.moveCombinationQuantity(true);
        final List<Creature> creatures = new ArrayList<>(combinationsQuantity);
        for (int i = 0; i < combinationsQuantity; i++) {
            creatures.add(creatureFromMoveCombinationId(species, this.opponent, i));
        }
        return creatures;
    }

    private Creature creatureFromMoveCombinationId(Species species, Opponent opponent, int moveCombinationId) {
        final Move[] moveCombination = species.moveCombinationFromId(moveCombinationId, true);

        if (opponent.getTitle().isRocket()) {
            return new Creature(
                species, 
                opponent.getTitle(), 
                this.trainerLevel, 
                moveCombination[0], 
                moveCombination[1]
            );
        } else {
            return new Creature(
                species, 
                Stats3.getMaxIVs(), 
                55, 
                moveCombination[0], 
                List.of(moveCombination[1])
            );
        }
    }

    private static String createEnemyTeamKey(Team<Creature> enemyTeam, boolean[] reachableChargedMoves) {
        final StringBuilder keyBuilder = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            final Creature c = enemyTeam.getByInt(i+1);
            if (c == null) break;

            keyBuilder.append(c.getSpecies().getSpeciesId());
            keyBuilder.append(c.getFastMove().moveId());
            
            if (reachableChargedMoves[i] && !c.getChargedMoves().isEmpty()) {
                keyBuilder.append(c.getChargedMoves().get(0).moveId());
            }
        }
        return keyBuilder.toString();
    }

    @Override
    public BattleResult getBattleResult() {
        return this.battleResult;
    }
}
