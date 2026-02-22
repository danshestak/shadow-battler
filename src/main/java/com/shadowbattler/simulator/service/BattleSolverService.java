package com.shadowbattler.simulator.service;

import org.springframework.stereotype.Service;

import com.shadowbattler.simulator.model.Creature;
import com.shadowbattler.simulator.model.Team;
import com.shadowbattler.simulator.model.battle.BattleResult;
import com.shadowbattler.simulator.model.battle.BattleSolver;
import com.shadowbattler.simulator.model.battle.TeamBattleSolver;

@Service
public class BattleSolverService {
    private final OpponentDataService opponentDataService;

    public BattleSolverService(OpponentDataService opponentDataService) {
        this.opponentDataService = opponentDataService;
    }

    public BattleResult solveTeamBattle(Team<Creature> playerTeam, Team<Creature> opponentTeam, int opponentStartingShields) {
        final BattleSolver teamBattleSolver = new TeamBattleSolver(playerTeam, opponentTeam, opponentStartingShields);
        teamBattleSolver.solve();

        return teamBattleSolver.getBattleResult();
    }
}
