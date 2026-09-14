import { getMoves, getOpponents, getSpecies } from '@/lib/serverData';
import BattleClientPage from './BattleClientPage';
import { OpponentCreature, PlayerCreature } from '@/types/Creature';
import { Opponent } from '@/types/Opponent';

interface CountersPageProps {
  searchParams: Promise<{ [key: string]: string | string[] | undefined }>
}

const noArr = (s: string | string[] | undefined) => Array.isArray(s) ? (s.length > 0 ? s[0] : undefined) : s;

const BattlePage = async ({ searchParams }: CountersPageProps) => {
  const resolvedParams = await searchParams;
  const opponents = await getOpponents();
  const species = await getSpecies();
  const moves = await getMoves();

  const solveForMoveset = Boolean(resolvedParams.solve_for_moveset);
  const enemyMode = (['team', 'lineup'] as const).find(v => v === resolvedParams.enemy_mode) ?? 'team'
  const opponent: Opponent | null = opponents[noArr(resolvedParams.opponent) ?? ''] ?? null;
  const trainerLevel = (x => (x < 8 || x > 80 || !Number.isInteger(x)) ? 70 : x)(Number(resolvedParams.trainer_level));

  const playerCreature = PlayerCreature.fromSearchParams(species, moves, noArr(resolvedParams.p1) ?? '');
  const enemyCreatures = (['first', 'second', 'third'] as const).map((v, i) => {
    if (opponent === null) {
      return OpponentCreature.new(trainerLevel, 'ROCKET_GRUNT');
    } else {
      const c = OpponentCreature.fromSearchParams(species, moves, noArr(resolvedParams[`e${i+1}`]) ?? '', trainerLevel, opponent.title);
      return (!c.species || opponent.lineup[v].includes(c.species.speciesId)) ? c : OpponentCreature.new(trainerLevel, 'ROCKET_GRUNT');
    }
  });

  return (
    <div className="max-w-3xl m-auto">
      <h1 className="text-2xl mb-4">Battle</h1>

      <p className="mb-4">
        Simulate battles between a Pokémon and an opponent team or lineup.
      </p>

      <BattleClientPage
        initPlayerCreature={playerCreature}
        initEnemyCreature1={enemyCreatures[0]}
        initEnemyCreature2={enemyCreatures[1]}
        initEnemyCreature3={enemyCreatures[2]}
        initSolveForMoveset={solveForMoveset}
        initEnemyMode={enemyMode}
        initOpponent={opponent}
        initTrainerLevel={trainerLevel}
      />
    </div>
  )
}

export default BattlePage