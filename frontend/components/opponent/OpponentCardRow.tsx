import React from 'react'
import Sprite from '../sprite/Sprite';
import { Species } from '@/types/Species';
import { cn } from '@/lib/utils';
import CombatPowerLabel from '../CombatPowerLabel';
import { OpponentTitle } from '@/types/Opponent';

interface OpponentCardRowProps {
    speciesArr?: Species[];
    slotNumber?: number;
    asteriskCount?: number;
    className?: string;
    cpDependencies?: { trainerLevel: number, title: OpponentTitle }
}

const OpponentCardRow = ({ speciesArr, asteriskCount, slotNumber, className, cpDependencies }: OpponentCardRowProps) => {
  const arr = speciesArr ?? [undefined, undefined, undefined];

  return (
    <div>
      {slotNumber !== undefined && <div className='text-sm text-text'>Slot {slotNumber}:</div>}
      <div className={cn('grid grid-cols-3 min-h-24 bg-theme2 border border-theme4 rounded py-2', className)}>
      {
        arr.map((s, i) => (
          <div key={i} className='relative flex justify-center items-center'>
            {(cpDependencies !== undefined && s !== undefined) && <CombatPowerLabel
              cp={s ? Species.getCp(Species.getOpponentStats(s, cpDependencies.trainerLevel, cpDependencies.title)) : 0}
              className='absolute top-0 right-1 text-xs bg-theme3'
            />}

            {(s !== undefined) && <span className="absolute bottom-0 z-2 px-1 w-full text-center text-xs tracking-tight leading-none drop-shadow-theme2 drop-shadow">
              {s.speciesName}
              {((asteriskCount !== undefined) && (asteriskCount > 0)) && <span className='text-highlight'>{"*".repeat(asteriskCount)}</span>}
            </span>}

            <Sprite species={s} scale={2} />
          </div>
        ))
      }
      </div>
    </div>
  )
}

export default OpponentCardRow