'use client';

import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "./ui/dialog"
import Sprite from "./sprite/Sprite"
import TypeLabel from "./TypeLabel"
import MoveLabel from "./MoveLabel"
import SpeciesCombobox from "./battle/SpeciesCombobox"
import NumberCombobox from "./battle/NumberCombobox"
import MoveCombobox from "./battle/MoveCombobox"
import Meter from "./Meter"
import { Creature } from "@/types/Creature"
import { useState } from "react";
import { useClientData } from "@/lib/clientData";
import { Stats3 } from "@/types/Stats3";

const CreaturePanel = () => {
  const { clientData, isError, isLoading } = useClientData();
  const [creature, setCreature] = useState(new Creature());
  
  if (isError || isLoading) return <></>;
  
  const handleCreatureChange = <K extends keyof Creature>(key: K, value: Creature[K]) => {
    const clone = creature.clone();
    clone[key] = value;
    setCreature(clone);
  };

  const handleIvsChange = (key: keyof Stats3<number>, value: number) => {
    const clone = creature.clone();
    clone.ivs[key] = value;
    setCreature(clone);
  }

  return (
    <Dialog>
      <DialogTrigger>
        <div className='flex justify-between items-stretch p-2 text-sm text-start bg-theme1 border border-theme4 hover:border-highlight transition rounded shadow-lg cursor-pointer'>
          <div className='flex flex-col grow gap-1 items-stretch'>
            <h4 className='text-base'>
              {(creature.species?.speciesName ?? 'None') + ' '}
              {creature.species && <span className='text-nowrap text-sm tracking-tight px-1 rounded border border-theme4 bg-theme3'>
                {creature.cp} CP
              </span>}
            </h4>

            {!creature.species && <div className="italic">
              Click to select a Pokémon!
            </div>}

            <div className='flex gap-0.5 mb-2'>
              {creature.species?.types
                .filter(t => t !== 'NONE')
                .map((t, i) => <TypeLabel type={t} key={i}/>)}
            </div>

            <div className='flex flex-wrap gap-0.5 items-start text-xs'>
              {
              [creature.fast, creature.charged1, creature.charged2]
                .filter(v => v !== undefined)
                .map((m, i) => <MoveLabel move={m} species={creature.species} key={i}/>)
              }
            </div>
          </div>
          <div className="flex items-center">
            <Sprite species={creature.species} scale={2} />
          </div>
        </div>
      </DialogTrigger>
      <DialogContent className="flex flex-col sm:max-w-sm" showCloseButton={false}>
        <DialogHeader>
          <DialogTitle>Edit Pokémon</DialogTitle>
        </DialogHeader>

        <div className='flex justify-between items-center'>
          <span>Species</span>
          <SpeciesCombobox 
            speciesData={clientData.species}
            value={creature.species?.speciesId} 
            onValueChange={(id) => handleCreatureChange('species', id !== null ? clientData.species[id] : undefined)}
          />
        </div>

        <div className='flex justify-between items-center'>
          <span>Level</span>
          <NumberCombobox 
            value={creature.level}
            onValueChange={(v) => v !== null ? handleCreatureChange('level', v) : undefined}
            min={1} 
            max={51} 
            step={0.5}
          />
        </div>

        <div className='flex justify-between items-center'>
          <span>IVs (ATK/DEF/HP)</span>
          <span className="flex flex-wrap items-center justify-end gap-0.5">
            <NumberCombobox
              value={creature.ivs.atk}
              onValueChange={(v) => v !== null ? handleIvsChange('atk', v) : undefined}
              min={0} 
              max={15}
            />
            /
            <NumberCombobox 
              value={creature.ivs.def}
              onValueChange={(v) => v !== null ? handleIvsChange('def', v) : undefined}
              min={0} 
              max={15}
            />
            /
            <NumberCombobox 
              value={creature.ivs.hp}
              onValueChange={(v) => v !== null ? handleIvsChange('hp', v) : undefined}
              min={0} 
              max={15}
            />
          </span>
        </div>

        <div className="p-2 bg-theme3 shadow-lg border border-theme4 rounded text-xs grid grid-cols-[1fr_1fr_8fr] items-center gap-2">
          <div className='text-sm'>CP:</div>
          <div className='col-span-2'>{creature.cp}</div>

          <div className='text-sm'>ATK:</div>
          <div>{creature.stats.atk.toFixed(1)}</div>
          <Meter value={creature.stats.atk} max={400} colorSensitivity={2} colorOffset={100} label="Attack" className='h-2.5' />

          <div className='text-sm'>DEF:</div>
          <div>{creature.stats.def.toFixed(1)}</div>
          <Meter value={creature.stats.def} max={400} colorSensitivity={2} colorOffset={100} label="Defense" className='h-2.5' />

          <div className='text-sm'>HP:</div>
          <div>{creature.stats.hp}</div>
          <Meter value={creature.stats.hp} max={400} colorSensitivity={2} colorOffset={100} label="HP" className='h-2.5' />
        </div>

        <div className='flex justify-between items-center pt-2 border-t border-theme4'>
          <span>Fast move</span>
          <MoveCombobox 
            movesData={clientData.moves}
            userSpecies={creature.species}
            selectableMoveIds={creature.species?.fastMoves ?? []}
            value={creature.fast?.moveId}
            onValueChange={(id) => handleCreatureChange('fast', id !== null ? clientData.moves[id] : undefined)}
            disabled={creature.species === undefined}
          />
        </div>

        <div className='flex justify-between items-center'>
          <span>Charged move 1</span>
          <MoveCombobox 
            movesData={clientData.moves}
            userSpecies={creature.species}
            selectableMoveIds={creature.species?.chargedMoves.filter(id => id !== creature.charged2?.moveId) ?? []}
            value={creature.charged1?.moveId}
            onValueChange={(id) => handleCreatureChange('charged1', id !== null ? clientData.moves[id] : undefined)}
            disabled={creature.species === undefined}
          />
        </div>

        <div className='flex justify-between items-center'>
          <span>Charged move 2</span>
          <MoveCombobox 
            movesData={clientData.moves}
            userSpecies={creature.species}
            selectableMoveIds={creature.species?.chargedMoves.filter(id => id !== creature.charged1?.moveId) ?? []}
            value={creature.charged2?.moveId}
            onValueChange={(id) => handleCreatureChange('charged2', id !== null ? clientData.moves[id] : undefined)}
            disabled={creature.species === undefined}
          />
        </div>
      </DialogContent>
    </Dialog>
  )
}

export default CreaturePanel