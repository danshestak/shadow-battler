import React from 'react'
import Sprite from '../sprite/Sprite';
import { Species } from '@/types/Species';

interface OpponentCardRowProps {
    speciesArr: Species[];
    asteriskCount?: number
}

const OpponentCardRow = ({ speciesArr, asteriskCount }: OpponentCardRowProps) => {
  return (
    <div className='grid grid-cols-3 h-24 bg-theme2 border border-theme4 rounded'>
      {
        speciesArr.map((s, i) => (
          <div key={i} className="relative flex justify-center items-end p-1">
            <span className="absolute top-1 w-full text-center text-xs tracking-tight leading-none">
              {s.id.replaceAll("_", " ")}
              {((asteriskCount !== undefined) && (asteriskCount > 0)) && <span className='text-highlight'>{"*".repeat(asteriskCount)}</span>}
            </span>
            <Sprite species={s} scale={2} />
          </div>
        ))
      }
    </div>
  )
}

export default OpponentCardRow