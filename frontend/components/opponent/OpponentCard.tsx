import OpponentCardRow from './OpponentCardRow'
import Link from 'next/link'
import TypeLabel from '../TypeLabel'
import { Opponent, OpponentTitle } from '@/types/Opponent'
import { Lineup } from '@/types/Lineup'
import { getSpecies } from '@/lib/serverData'
import { buttonVariants } from '../ui/button'
import { cn } from '@/lib/utils'

interface OpponentCardProps {
  opponent: Opponent
}

const OpponentCard = async ({ opponent }: OpponentCardProps) => {
  const species = await getSpecies();
  
  return (
  <div className='p-2 bg-theme3 border border-theme4 rounded shadow-lg'>
    <div className='flex justify-between items-center border-b border-theme4 pb-2'>
      <div>
        <h2 className='text-xl'>{opponent.name}</h2>
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

        <div className='pt-2 flex justify-end gap-2'>
          <Link
            href={`/battle/?opponent=${opponent.opponentId}&enemy_mode=lineup`}
            className={cn(buttonVariants(), 'py-1')}
          >
            Battle
          </Link>
          <Link
            href={`/counters/${opponent.opponentId}`}
            className={cn(buttonVariants({ variant: 'highlight' }), 'py-1')}
          >
            View counters
          </Link>
        </div>
  </div>
  )
}

export default OpponentCard