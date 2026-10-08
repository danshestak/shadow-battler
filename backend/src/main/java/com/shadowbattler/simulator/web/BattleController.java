package com.shadowbattler.simulator.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shadowbattler.simulator.model.Creature;
import com.shadowbattler.simulator.model.Move;
import com.shadowbattler.simulator.model.Opponent;
import com.shadowbattler.simulator.model.Species;
import com.shadowbattler.simulator.model.Team;
import com.shadowbattler.simulator.model.battle.MovesetSolver;
import com.shadowbattler.simulator.model.battle.OpponentBattleSolver;
import com.shadowbattler.simulator.model.battle.TeamBattleSolver;
import com.shadowbattler.simulator.persistence.service.BattleResultEntityService;
import com.shadowbattler.simulator.service.BattlePersistenceService;
import com.shadowbattler.simulator.service.MovesDataService;
import com.shadowbattler.simulator.service.OpponentDataService;
import com.shadowbattler.simulator.service.SpeciesDataService;
import com.shadowbattler.simulator.web.dto.BattleRequest;
import com.shadowbattler.simulator.web.dto.CreatureDTO;

@RestController
@RequestMapping("/api")
public class BattleController {
    private final SpeciesDataService speciesDataService;
    private final MovesDataService movesDataService;
    private final OpponentDataService opponentDataService;
    private final BattleResultEntityService battleResultEntityService;

    public BattleController(
        SpeciesDataService speciesDataService,
        MovesDataService movesDataService,
        OpponentDataService opponentDataService,
        BattleResultEntityService battleResultEntityService
    ) {
        this.speciesDataService = speciesDataService;
        this.movesDataService = movesDataService;
        this.opponentDataService = opponentDataService;
        this.battleResultEntityService = battleResultEntityService;
    }

    public class HydrateCreatureException extends RuntimeException {
        public HydrateCreatureException(String message) {
            super(message);
        }
    }

    private Creature hydrateCreatureDto(CreatureDTO dto, BattleRequest battleRequest, boolean ignoreMoves, Opponent hydratedOpponent, int opponentSlot) {
        if (dto == null) throw new HydrateCreatureException("Cannot be null");

        final Species species;
        try {
            species = this.speciesDataService.getSpeciesById(dto.species().toLowerCase());
        } catch (Exception e) {
            throw new HydrateCreatureException("Invalid species ID");
        }

        Move fast = null;
        if (!ignoreMoves) {
            try {
                fast =  this.movesDataService.getMoveById(dto.fast().toUpperCase());
            } catch (Exception e) {
                throw new HydrateCreatureException("Invalid fast move ID");
            }
        }
        if (!species.getFastMoves().contains(fast)) {
            throw new HydrateCreatureException("Invalid fast move");
        }

        Move charged1 = null;
        if (!ignoreMoves) {
            try {
                charged1 =  this.movesDataService.getMoveById(dto.charged1().toUpperCase());
            } catch (Exception e) {
                throw new HydrateCreatureException("Invalid charged move 1 ID");
            }
        }
        if (!species.getChargedMoves().contains(charged1)) {
            throw new HydrateCreatureException("Invalid charged move 1");
        }

        if (dto.user() == CreatureDTO.User.PLAYER) {
            if (dto.level() == null || dto.level() < 1 || dto.level() > 55 || dto.level() % 0.5 != 0) {
                throw new HydrateCreatureException("Invalid level");
            }

            if (dto.ivs() == null || dto.ivs().toStream().anyMatch(iv -> iv < 0 || iv > 15)) {
                throw new HydrateCreatureException("Invalid IVs");
            }


            final List<Move> moves = new ArrayList<>();
            moves.add(charged1);
            if (!ignoreMoves || dto.charged2() != null) {
                Move charged2 = null;
                try {
                    charged2 = this.movesDataService.getMoveById(dto.charged2().toUpperCase());
                } catch (Exception e) {
                    throw new HydrateCreatureException("Invalid charged move 2 ID");
                }
                if (!species.getChargedMoves().contains(charged2)) {
                    throw new HydrateCreatureException("Invalid charged move 2");
                }
                moves.add(charged2);
            }

            return new Creature(species, dto.ivs(), dto.level(), fast, !ignoreMoves ? moves : null);
        } else {
            if (hydratedOpponent == null) {
                throw new HydrateCreatureException("Invalid opponent");
            }

            if (!hydratedOpponent.getLineupSpecies().getByInt(opponentSlot).contains(species)) {
                throw new HydrateCreatureException("Invalid species");
            }

            return new Creature(species, hydratedOpponent.getTitle(), battleRequest.trainerLevel(), fast, charged1);
        }
    }

    @PostMapping("/battle")
    public ResponseEntity<?> requestBattle(@RequestBody BattleRequest battleRequest) {
        final Opponent opponent;
        try {
            opponent = this.opponentDataService.getOpponentById(battleRequest.opponentId());
        } catch (Exception e) {
            return new ResponseEntity<>("Opponent not found", HttpStatus.BAD_REQUEST);
        }
        
        if (battleRequest.trainerLevel() < 8 || battleRequest.trainerLevel() > 80) {
            return new ResponseEntity<>("Invalid trainer level", HttpStatus.BAD_REQUEST);
        }

        if (battleRequest.solveForMoveset() && battleRequest.enemyMode() == BattleRequest.EnemyMode.LINEUP) {
            if (!BattlePersistenceService.TRAINER_LEVELS.contains(battleRequest.trainerLevel())) {
                return new ResponseEntity<>("Invalid trainer level for lineup moveset solver", HttpStatus.BAD_REQUEST);
            }
            if (!BattlePersistenceService.PLAYER_CREATURE_LEVELS.contains(battleRequest.playerCreature().level())) {
                return new ResponseEntity<>("Invalid trainer level for lineup moveset solver", HttpStatus.BAD_REQUEST);
            }
        }

        final Creature playerCreature;
        try {
            playerCreature = this.hydrateCreatureDto(battleRequest.playerCreature(), battleRequest, battleRequest.solveForMoveset(), opponent, -1);
        } catch (HydrateCreatureException e) {
            return new ResponseEntity<>("Player creature error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        if (battleRequest.enemyMode() == BattleRequest.EnemyMode.TEAM) {
            final Creature[] enemyCreatures = new Creature[] {null, null, null};
            final CreatureDTO[] enemyDtos = new CreatureDTO[] {
                battleRequest.enemyCreature1(), 
                battleRequest.enemyCreature2(), 
                battleRequest.enemyCreature3()
            };

            for (int i = 0; i < 3; i++) {
                try {
                    enemyCreatures[i] = this.hydrateCreatureDto(enemyDtos[i], battleRequest, false, opponent, i+1);
                } catch (HydrateCreatureException e) {
                    return new ResponseEntity<>(String.format("Enemy creature %d error: %s", i+1, e.getMessage()), HttpStatus.BAD_REQUEST);
                }
            }

            if (battleRequest.solveForMoveset()) {
                final var enemyTeam = new Team<>(enemyCreatures);
                final var solver = new MovesetSolver(
                    (fast, charged) -> new Creature(
                        playerCreature.getSpecies(),
                        playerCreature.getIvs(),
                        battleRequest.playerCreature().level(),
                        fast,
                        charged
                    ), (movesetCreature) -> new TeamBattleSolver(
                        movesetCreature, 
                        enemyTeam, 
                        opponent.getTitle().getShields()
                    )
                );
                solver.solve();
                return ResponseEntity.ok(solver.getBattleResults());
            } else {
                final var solver = new TeamBattleSolver(
                    playerCreature, 
                    new Team<>(enemyCreatures), 
                    opponent.getTitle().getShields()
                );
                solver.solve();
                return ResponseEntity.ok(List.of(solver.getBattleResult()));
            }
        } else {
            if (battleRequest.solveForMoveset()) {
                final var results = this.battleResultEntityService.getMovesetBRs(
                    playerCreature.getSpecies().getSpeciesId(),
                    opponent.getOpponentId()
                );
                return ResponseEntity.ok(results);
            } else {
                final var solver = new OpponentBattleSolver(
                    new Team<>(playerCreature, null, null), 
                    opponent, 
                    battleRequest.trainerLevel()
                );
                solver.solve();
                return ResponseEntity.ok(List.of(solver.getBattleResult()));
            }
        }
    }
}
