'use client';

import NumberSelect, { NumberSelectPlaceholder } from "@/components/battle/NumberSelect";
import OpponentCombobox from "@/components/battle/OpponentCombobox";
import CreaturePanel from "@/components/CreaturePanel"
import OpponentCardRow from "@/components/opponent/OpponentCardRow";
import { Button, buttonVariants } from "@/components/ui/button";
import { HoverCard, HoverCardContent, HoverCardTrigger } from "@/components/ui/hover-card";
import { Switch } from "@/components/ui/switch";
import { ToggleGroup, ToggleGroupItem } from "@/components/ui/toggle-group";
import { useClientData } from "@/lib/clientData";
import { PlayerCreature, OpponentCreature, Creature } from "@/types/Creature";
import { Lineup } from "@/types/Lineup";
import { Opponent } from "@/types/Opponent";
import { cn } from "cn";
import { useMemo, useState } from "react";

const panelClass = 'bg-theme3 border border-theme4 rounded p-2 flex flex-col gap-2 shadow-lg flex-1 text-sm';
const betweenClass = 'flex justify-between items-center gap-2';
const creaturePanelMinH = 'min-h-25';
const enemyCreaturePanelDisabledMsg = 'No opponent selected'

interface BattleClientPageProps {
  initPlayerCreature: PlayerCreature,
  initEnemyCreature1: OpponentCreature,
  initEnemyCreature2: OpponentCreature,
  initEnemyCreature3: OpponentCreature,
  initSolveForMoveset: boolean,
  initEnemyMode: 'team' | 'lineup',
  initOpponent: Opponent | null,
  initTrainerLevel: number
} 

const BattleClientPage = (
  {
    initPlayerCreature,
    initEnemyCreature1,
    initEnemyCreature2,
    initEnemyCreature3,
    initSolveForMoveset,
    initEnemyMode,
    initOpponent,
    initTrainerLevel
  }: BattleClientPageProps
) => {
  const { clientData, isError, isLoading } = useClientData();
  const isReady = !isError && !isLoading;

  const [playerCreature, setPlayerCreature] = useState(initPlayerCreature);
  const [enemyCreature1, setEnemyCreature1] = useState(initEnemyCreature1);
  const [enemyCreature2, setEnemyCreature2] = useState(initEnemyCreature2);
  const [enemyCreature3, setEnemyCreature3] = useState(initEnemyCreature3);
  const enemyCreatureStates = [[enemyCreature1, setEnemyCreature1], [enemyCreature2, setEnemyCreature2], [enemyCreature3, setEnemyCreature3]] as const;

  const setEnemyCreatures = (transform: (c: OpponentCreature, i: number) => void) => {
    enemyCreatureStates.forEach((arr, i) => {
      const clone = { ...arr[0] };
      transform(clone, i);
      arr[1](clone);
    });
  };
  
  const [solveForMoveset, setSolveForMoveset] = useState(initSolveForMoveset);
  const [enemyMode, setEnemyMode] = useState(initEnemyMode);
  const [opponent, setOpponent] = useState(initOpponent);
  const statefulSetOpponent = (v: Opponent | null) => {
    const lineupArr = v !== null ? Lineup.toArray(v.lineup) : [];

    setEnemyCreatures((c, i) => {
      if (v !== null) {
        c.title = v.title;
        if (lineupArr[i].length === 1) {
          OpponentCreature.statefulSetSpecies(c, clientData.species[lineupArr[i][0]]);
          c.fast = undefined;
          c.charged1 = undefined;
        } else {
          OpponentCreature.statefulSetSpecies(c, undefined)
        }
      } else {
        OpponentCreature.statefulSetSpecies(c, undefined);
        c.title = 'ROCKET_GRUNT';
      }
    });

    setOpponent(v);
  }
  const [trainerLevel, setTrainerLevel] = useState<number>(initTrainerLevel);
    const statefulSetTrainerLevel: typeof setTrainerLevel = (v) => {
    setEnemyCreatures((c) => {
      c.trainerLevel = typeof v === 'number' ? v : initTrainerLevel;
    });
    setTrainerLevel(v);
  }

  const issues = useMemo(() => {
    if (!isReady) return ['Data is not loaded'];

    const currIssues = [];

    const checkCreatureForIssues = (c: Creature, name: string) => {
      if (c.species === undefined) {
        currIssues.push(`${name} is missing species`);
      } else {
        if (!(c.user === 'player' && solveForMoveset)) {
          if (c.fast === undefined) {
            currIssues.push(`${name} is missing fast move`);
          }
          if (c.user === 'player') {
            if (c.charged1 === undefined && (c as PlayerCreature).charged2 === undefined) {
              currIssues.push(`${name} is missing charged move(s)`)
            }
          } else {
            if (c.charged1 === undefined) {
              currIssues.push(`${name} is missing charged move`)
            }
          }
        }
      }
    }

    checkCreatureForIssues(playerCreature, 'Player Pokémon');

    if (opponent === null) {
      currIssues.push('No opponent is selected');
    } else {
      if (enemyMode === 'team') {
        const enemyCreatures = [enemyCreature1, enemyCreature2, enemyCreature3] as const;
        for (let i = 0; i < enemyCreatures.length; i++) {
          checkCreatureForIssues(enemyCreatures[i], `Enemy Pokémon in slot ${i+1}`)
        }
      }
    }

    return currIssues;
  }, [playerCreature, enemyCreature1, enemyCreature2, enemyCreature3, solveForMoveset, enemyMode, opponent, isReady])

  return (
    <div>
      <div className='flex flex-col items-stretch md:flex-row gap-4 pb-2'>
        <div className={panelClass}>
          <h2 className='text-xl'>Player</h2>
          <div className={betweenClass}>
            Solve for moveset:
            <Switch checked={solveForMoveset} onCheckedChange={setSolveForMoveset}/>
          </div>
          <CreaturePanel 
            creature={playerCreature} 
            setCreature={setPlayerCreature} 
            hide={solveForMoveset ? ['moves'] : []}
            className={creaturePanelMinH}
          />
        </div>

        <div className='self-stretch flex items-center justify-center font-bold tracking-tight'>
          VS.
        </div>

        <div className={panelClass}>
          <div className={betweenClass}>
            <span className='text-xl'>Enemy</span>

            <ToggleGroup 
              value={[enemyMode]} 
              onValueChange={(v, eventDetails) => v.length === 0 ? eventDetails.cancel() : setEnemyMode(v[0] as 'team' | 'lineup')} 
              defaultValue={['team']} 
              spacing={0}
            >
              <ToggleGroupItem value='team' aria-label='Toggle team' className='aria-pressed:pointer-events-none'>
                Team
              </ToggleGroupItem>
              <ToggleGroupItem value='lineup' aria-label='Toggle lineup' className='aria-pressed:pointer-events-none'>
                Lineup
              </ToggleGroupItem>
            </ToggleGroup>
          </div>
          <div className={betweenClass}>
            Opponent:
            <OpponentCombobox 
              opponentsData={clientData?.opponents ?? {}} 
              onValueChange={(id) => statefulSetOpponent(id !== null ? (clientData?.opponents[id] ?? null) : null)} 
              value={opponent !== null ? opponent.name : null}
            />
          </div>
          <div className={betweenClass}>
            Trainer level:
            {isReady ? <NumberSelect 
              min={8} 
              max={80} 
              value={trainerLevel} 
              onValueChange={(v) => v !== null && statefulSetTrainerLevel(v)}
              disabled={!isReady || opponent === null}
            /> : <NumberSelectPlaceholder value={trainerLevel}/>}
          </div>
          {enemyMode === 'team' ? <>
            <CreaturePanel 
              species={opponent?.lineup.first}
              creature={enemyCreature1}
              setCreature={c => setEnemyCreature1(c)}
              disabled={opponent === null}
              disabledMessage={enemyCreaturePanelDisabledMsg}
              className={creaturePanelMinH}
            />
            <CreaturePanel 
              species={opponent?.lineup.second}
              creature={enemyCreature2} 
              setCreature={c => setEnemyCreature2(c)}
              disabled={opponent === null}
              disabledMessage={enemyCreaturePanelDisabledMsg}
              className={creaturePanelMinH}
            />
            <CreaturePanel 
              species={opponent?.lineup.third}
              creature={enemyCreature3} 
              setCreature={c => setEnemyCreature3(c)}
              disabled={opponent === null}
              disabledMessage={enemyCreaturePanelDisabledMsg}
              className={creaturePanelMinH}
            />
          </> : <>
            <OpponentCardRow 
              speciesArr={opponent?.lineup.first.map(id => clientData?.species[id]) ?? undefined}
              className={creaturePanelMinH}
              cpDependencies={{ trainerLevel: trainerLevel, title: opponent?.title ?? 'ROCKET_GRUNT' }}
            />
            <OpponentCardRow 
              speciesArr={opponent?.lineup.second.map(id => clientData?.species[id]) ?? undefined}
              className={creaturePanelMinH}
              cpDependencies={{ trainerLevel: trainerLevel, title: opponent?.title ?? 'ROCKET_GRUNT' }}
            />
            <OpponentCardRow 
              speciesArr={opponent?.lineup.third.map(id => clientData?.species[id]) ?? undefined}
              className={creaturePanelMinH}
              cpDependencies={{ trainerLevel: trainerLevel, title: opponent?.title ?? 'ROCKET_GRUNT' }}
            />
          </>
          }
        </div>
      </div>
      <div className='flex justify-end'>
        <HoverCard>
          <HoverCardTrigger className='w-full md:max-w-60 h-8 flex items-stretch' delay={100}>
            {isReady ? <Button className='h-full w-full hover:bg-theme1' variant='highlight' disabled={issues.length > 0}>
              Simulate battle!
            </Button> : <div className={cn(buttonVariants({ variant: 'highlight' }), 'h-full w-full pointer-events-none opacity-50')} aria-disabled={true}>
              Simulate battle!
            </div>}
          </HoverCardTrigger>
          <HoverCardContent hidden={issues.length === 0}>
            <h3 className="mb-1">Missing fields!</h3>
            <ul className="list-disc list-inside text-sm">
              {issues.map((s, i) => <li key={i}>{s}</li>)}
            </ul>
          </HoverCardContent>
        </HoverCard>
      </div>
    </div>
  )
}

export default BattleClientPage