'use client';

import React from 'react';
import OpponentCardRow from './OpponentCardRow'
import Link from 'next/link'
import TypeLabel from '../TypeLabel'
import { Opponent, OpponentTitle } from '@/types/Opponent'
import { Lineup } from '@/types/Lineup'
import { useSpecies } from '@/context/SpeciesContext';

interface OpponentCardProps {
    opponent: Opponent
}

const OpponentCard = ({ opponent }: OpponentCardProps) => {
  const species = useSpecies();
  
  return (
    <div className='p-2 bg-theme3 border border-theme4 rounded shadow-lg'>
        <div className='flex justify-between items-center border-b border-theme4 pb-2'>
            <div>
                <div className='text-xl'>{opponent.name}</div>
                <div className='text-sm italic'>{OpponentTitle.toFull(opponent.title)}</div>
            </div>

            <TypeLabel type={opponent.specialtyType ?? "NONE"}/>
        </div>

        <div className='grid grid-rows-3 pt-2 gap-2 border-b border-theme4 pb-2'>
            {Lineup.toArray(opponent.lineup).map((speciesIds, i) => 
                <OpponentCardRow 
                key={i} 
                speciesArr={speciesIds.map(id => species[id])} 
                slotNumber={i+1} 
                asteriskCount={opponent.encounterSlots.includes(i+1) ? (i+1 === 2 ? 2 : 1) : undefined}
                />
            )}
        </div>

        <div className='pt-2 flex justify-end'>
            <Link href="/counters/normal_type_grunt" className='
            bg-highlight p-2 rounded border border-transparent shadow
            hover:bg-theme2 hover:border-highlight transition
            active:border-text'
            >View counters &#9656;
            </Link>
        </div>
    </div>
  )
}

export default OpponentCard