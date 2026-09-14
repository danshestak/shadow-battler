'use client';

import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "./ui/dialog"
import Sprite from "./sprite/Sprite"
import TypeLabel from "./TypeLabel"
import MoveLabel from "./MoveLabel"
import SpeciesCombobox from "./battle/SpeciesCombobox"
import NumberSelect from "./battle/NumberSelect"
import MoveCombobox from "./battle/MoveCombobox"
import Meter from "./Meter"
import { Creature, OpponentCreature, PlayerCreature } from "@/types/Creature"
import { Stats3 } from "@/types/Stats3";
import { useClientData } from "@/lib/clientData";
import { Species } from "@/types/Species";
import { cn } from "@/lib/utils";
import CombatPowerLabel from "./CombatPowerLabel";
import { buttonVariants } from "./ui/button";
import { useMemo } from "react";

const triggerClass = 'flex justify-between items-stretch p-2 text-sm text-start bg-theme1 border border-theme4 transition rounded shadow-lg';

const getText = (isLoading: boolean, isError: boolean, isDisabled: boolean, disabledMessage: string) => {
  if (isLoading) return 'Loading data...';
  if (isError) return 'Error loading data!';
  if (!isDisabled) return 'Click to select a Pokémon!';
  return disabledMessage;
}

interface CreaturePanelProps<T extends Creature> {
  species?: string[];
  creature: T;
  setCreature: React.Dispatch<React.SetStateAction<T>>;
  hide?: ('moves')[]
  disabled?: boolean
  disabledMessage?: string
  className?: string
}

function CreaturePanel<T extends Creature>({ species, creature, setCreature, hide = [], disabled = false, disabledMessage = '', className }: CreaturePanelProps<T>) {
  const { clientData, isError, isLoading } = useClientData();
  const c = creature.user === 'player' ? creature as unknown as PlayerCreature : creature as unknown as OpponentCreature;
  const isDisabled = disabled || isError || isLoading;
  const isReady = !isLoading && !isError;

  const stats = useMemo(() => {
    if (c.user === 'player') {
      return PlayerCreature.getStats(c);
    } else {
      return OpponentCreature.getStats(c);
    }
  }, [c]);

  const cp = useMemo(() => {
    return Species.getCp(stats);
  }, [stats]);

  const handleCreatureChange = (transform: (currCreature: typeof c) => void) => {
    const clone = { ...c };
    transform(clone);
    setCreature(clone as unknown as T);
  };

  const handleIvsChange = (key: keyof Stats3<number>, value: number) => {
    const clone = { ...c };
    clone.ivs[key] = value;
    setCreature(clone as unknown as T);
  }

  let speciesData: Record<string, Species> = {};
  if (isReady) {
    if (species !== undefined) {
      for (const id of species) {
        if (clientData.species.hasOwnProperty(id)) {
          speciesData[id] = clientData.species[id];
        } else {
          console.warn(`species ${id} not found in client data`);
        }
      }
    } else {
      speciesData = clientData.species;
    }
  }

  const moves = [c.fast, c.charged1];
  if (c.user === 'player') moves.push(c.charged2);

  const triggerContent = (
    <>
      <div className='flex flex-col grow gap-1 items-stretch'>
        <h4 className='text-base'>
          {(c.species?.speciesName ?? '???') + ' '}
          {c.species && <CombatPowerLabel cp={cp}/>}
        </h4>

        {!c.species && <div className="italic">
          {getText(isLoading, isError, isDisabled, disabledMessage)}
        </div>}

        <div className='flex gap-0.5 mb-2'>
          {c.species?.types
            .filter(t => t !== 'NONE')
            .map((t, i) => <TypeLabel type={t} key={i} />)}
        </div>

        {(!hide.includes('moves') && c.species) && (
          <div className='flex flex-wrap gap-0.5 items-start text-xs'>
            {moves.map((m, i) => <MoveLabel move={m} species={c.species} key={i} />)}
          </div>
        )}
      </div>
      <div className="flex items-center">
        <Sprite species={c.species} scale={2} />
      </div>
    </>
  );

  return (
    <Dialog>
      {isReady ? (
        <DialogTrigger
          disabled={isDisabled}
          data-disabled={isDisabled}
          className={cn(buttonVariants(), triggerClass, className)}
        >
          {triggerContent}
        </DialogTrigger>
      ) : (
        <div className={cn(triggerClass, 'opacity-50', className)}>
          {triggerContent}
        </div>
      )}

      {isReady && (
        <DialogContent className="flex flex-col sm:max-w-sm" showCloseButton={false}>
          <DialogHeader>
            <DialogTitle>Edit Pokémon</DialogTitle>
          </DialogHeader>

          <div className='flex justify-between items-center'>
            <span>Species</span>
            <SpeciesCombobox
              speciesData={speciesData}
              value={c.species?.speciesId}
              onValueChange={(id) => handleCreatureChange((c) => {
                if (c.user === 'player') {
                  PlayerCreature.statefulSetSpecies(c, id !== null ? clientData.species[id] : undefined)
                } else {
                  OpponentCreature.statefulSetSpecies(c, id !== null ? clientData.species[id] : undefined)
                }
              })}
            />
          </div>

          {c.user === 'player' && (
            <>
              <div className='flex justify-between items-center'>
                <span>Level</span>
                <NumberSelect
                  value={c.level}
                  onValueChange={(v) => handleCreatureChange((c) => {
                    if (v === null) return;
                    (c as unknown as PlayerCreature).level = v;
                  })}
                  min={1}
                  max={51}
                  step={0.5}
                />
              </div>
              <div className='flex justify-between items-center'>
                <span>IVs (ATK/DEF/HP)</span>
                <span className="flex flex-wrap items-center justify-end gap-0.5">
                  <NumberSelect
                    value={c.ivs.atk}
                    onValueChange={(v) => v !== null && handleIvsChange('atk', v)}
                    min={0}
                    max={15}
                  />
                  /
                  <NumberSelect
                    value={c.ivs.def}
                    onValueChange={(v) => v !== null && handleIvsChange('def', v)}
                    min={0}
                    max={15}
                  />
                  /
                  <NumberSelect
                    value={c.ivs.hp}
                    onValueChange={(v) => v !== null && handleIvsChange('hp', v)}
                    min={0}
                    max={15}
                  />
                </span>
              </div>
            </>
          )}

          <div className="p-2 bg-theme3 shadow-lg border border-theme4 rounded text-xs grid grid-cols-[1fr_1fr_8fr] items-center gap-2">
            <div className='text-sm'>CP:</div>
            <div className='col-span-2'>{cp}</div>

            <div className='text-sm'>ATK:</div>
            <div>{stats.atk.toFixed(1)}</div>
            <Meter value={stats.atk} max={400} colorSensitivity={2} colorOffset={100} label="Attack" className='h-2.5' />

            <div className='text-sm'>DEF:</div>
            <div>{stats.def.toFixed(1)}</div>
            <Meter value={stats.def} max={400} colorSensitivity={2} colorOffset={100} label="Defense" className='h-2.5' />

            <div className='text-sm'>HP:</div>
            <div>{stats.hp}</div>
            <Meter value={stats.hp} max={400} colorSensitivity={2} colorOffset={100} label="HP" className='h-2.5' />
          </div>

          {c.user === 'opponent' && (
            <span className='text-center text-sm italic'>
              Team GO Rocket Pokémon stats depend on the player&apos;s trainer level
              and the enemy&apos;s title (i.e. grunt, leader, boss)
            </span>
          )}

          {!hide.includes('moves') && (
            <>
              <div className='flex justify-between items-center pt-2 border-t border-theme4'>
                <span>Fast move</span>
                <MoveCombobox
                  movesData={clientData.moves}
                  userSpecies={c.species}
                  selectableMoveIds={c.possibleFastMoves}
                  value={c.fast?.moveId}
                  onValueChange={(id) => handleCreatureChange((c) => {
                    c.fast = id !== null ? clientData.moves[id] : undefined;
                  })}
                  disabled={c.species === undefined}
                />
              </div>

              <div className='flex justify-between items-center'>
                <span>Charged move 1</span>
                <MoveCombobox
                  movesData={clientData.moves}
                  userSpecies={c.species}
                  selectableMoveIds={c.user === 'player' ? c.possibleChargedMoves.filter(id => id !== c.charged2?.moveId) : c.possibleChargedMoves}
                  value={c.charged1?.moveId}
                  onValueChange={(id) => handleCreatureChange((c) => {
                    c.charged1 = id !== null ? clientData.moves[id] : undefined;
                  })}
                  disabled={c.species === undefined}
                />
              </div>

              {c.user === 'player' && (
                <div className='flex justify-between items-center'>
                  <span>Charged move 2</span>
                  <MoveCombobox
                    movesData={clientData.moves}
                    userSpecies={c.species}
                    selectableMoveIds={c.possibleChargedMoves.filter(id => id !== c.charged1?.moveId)}
                    value={(c as unknown as PlayerCreature).charged2?.moveId}
                    onValueChange={(id) => handleCreatureChange((c) => {
                      (c as unknown as PlayerCreature).charged2 = id !== null ? clientData.moves[id] : undefined;
                    })}
                    disabled={c.species === undefined}
                  />
                </div>
              )}
            </>
          )}
        </DialogContent>
      )}
    </Dialog>
  )
}

export default CreaturePanel
