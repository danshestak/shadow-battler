import React from 'react'
import Sprite from '../sprite/Sprite';
import { Species } from '@/types/Species';

interface OpponentCardRowProps {
    speciesArr: Species[];
    slotNumber: number;
    asteriskCount?: number
}

const OpponentCardRow = ({ speciesArr, asteriskCount, slotNumber }: OpponentCardRowProps) => {
  return (
    <div>
      <div className='text-sm text-text'>Slot {slotNumber}:</div>
      <div className='grid grid-cols-3 h-24 bg-theme2 border border-theme4 rounded'>
      {
        speciesArr.map((s, i) => (
          <div key={i} className="relative flex justify-center items-center p-1">
            <span className="absolute bottom-1 z-2 w-full text-center text-xs tracking-tight leading-none drop-shadow-theme2 drop-shadow">
              {s.speciesName}
              {((asteriskCount !== undefined) && (asteriskCount > 0)) && <span className='text-highlight'>{"*".repeat(asteriskCount)}</span>}
            </span>
            <Sprite species={s} scale={2} />
          </div>
        ))
      }
    </div>
    </div>
  )
}

export default OpponentCardRow