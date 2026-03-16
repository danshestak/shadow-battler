package com.shadowbattler.simulator.service;

import org.springframework.stereotype.Service;

import com.shadowbattler.simulator.model.Creature;
import com.shadowbattler.simulator.model.Opponent;
import com.shadowbattler.simulator.model.Species;
import com.shadowbattler.simulator.model.Stats3;
import com.shadowbattler.simulator.model.Team;
import com.shadowbattler.simulator.model.battle.MovesetSolver;
import com.shadowbattler.simulator.model.battle.OpponentBattleSolver;
import com.shadowbattler.simulator.model.battle.TeamBattleSolver;

@Service
public class BattleSolverService {
    public BattleSolverService() {}

    public TeamBattleSolver solveTeamBattleSolver(Team<Creature> playerTeam, Team<Creature> opponentTeam, int opponentStartingShields) {
        final TeamBattleSolver teamBattleSolver = new TeamBattleSolver(playerTeam, opponentTeam, opponentStartingShields);
        teamBattleSolver.solve();
        return teamBattleSolver;
    }
    
    public OpponentBattleSolver solveOpponentBattleSolver(Team<Creature> playerTeam, Opponent opponent, int trainerLevel) {
        final OpponentBattleSolver opponentBattleSolver = new OpponentBattleSolver(playerTeam, opponent, trainerLevel);
        opponentBattleSolver.solve();
        return opponentBattleSolver;
    }

    public MovesetSolver solveOpponentMovesetSolver(Species playerSpecies, Stats3<Integer> playerIVs, int playerCreatureLevel, Opponent opponent, int trainerLevel) {
        final MovesetSolver opponentMovesetSolver = new MovesetSolver(
            (fast, charged) -> new Creature(playerSpecies, playerIVs, playerCreatureLevel, fast, charged),
            (playerCreature) -> new OpponentBattleSolver(
                new Team<>(playerCreature, null, null), 
                opponent,
                trainerLevel
            )
        );
        opponentMovesetSolver.solve();
        return opponentMovesetSolver;
    }
}
