import { cn } from '@/lib/utils'
import { CountersTableDescription, getGridColsByDesc } from './CountersTableDescription'
import CountersTableRow from './CountersTableRow'
import { BattleResult } from '@/types/BattleResult'
import { Opponent } from '@/types/Opponent'
import { ClientData } from '@/types/ClientData'
import { ChevronLeft, ChevronRight, Info, Settings2 } from 'lucide-react'
import { Button, buttonVariants } from '../ui/button'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '../ui/dialog'
import { Lineup } from '@/types/Lineup'
import OpponentCardRow from '../opponent/OpponentCardRow'
import CodeBlock from '../CodeBlock'
import { Switch } from '../ui/switch'
import { useMemo, useState } from 'react'
import NumberSelect from '../battle/NumberSelect'
import { useCountersFilters } from '@/hooks/useCountersFilters'

interface CountersTableProps {
  battleResults: BattleResult[]
  emptyTableMessage?: string,
  opponent: Opponent | null,
  clientData: ClientData | null,
  className?: string,
  title?: string,
  description: CountersTableDescription,
  useFilters: boolean
}

const colNames: Record<string, string> = {
  species: "Species",
  moves: "Moveset",
  time: "Time",
  winpercent: "Win%",
  score: "Score",
}

const pageSize = 20;
const legendaryTags = new Set(['LEGENDARY', 'MYTHICAL', 'ULTRABEAST']);
const outerCn = 'rounded bg-theme3 border border-theme4 p-2 overflow-x-auto shadow-lg';

const CountersTable = ({ battleResults, emptyTableMessage, opponent, clientData, className, title, description, useFilters }: CountersTableProps) => {
  const [page, setPage] = useState(1);
  const [{ includeMega, includeShadow, includeLegendary }, setFilter] = useCountersFilters();

  const filteredBattleResults = useMemo(() => {
    if (!battleResults || battleResults.length === 0) return [];
    if (!useFilters || (includeMega && includeShadow && includeLegendary)) return battleResults;
    return battleResults.filter(br => {
      for (const tag of br.playerSpecies.tags) {
        if (!includeMega && tag === 'MEGA') return false;
        if (!includeShadow && tag =='SHADOW') return false;
        if (!includeLegendary && legendaryTags.has(tag)) return false;
      }
      return true;
    });
  }, [battleResults, includeMega, includeShadow, includeLegendary, useFilters]);

  const battleResultsPage = useMemo(() => {
    const start = (page-1) * pageSize;
    const end = Math.min((page) * pageSize, filteredBattleResults.length);
    return filteredBattleResults.slice(start, end);
  }, [filteredBattleResults, page]);

  const pageCount = Math.max(1, Math.ceil(filteredBattleResults.length / pageSize));
  
  const gridCols = getGridColsByDesc(description);

  let firstCol: string | undefined = undefined;
  if (description.dropdownIndicator) {
    for (const e of Object.entries(description)) {
      if (e[1] && e[0] != "dropdownIndicator") {
        firstCol = e[0];
        break;
      }
    }
  }

  if (clientData === null) {
    return <div className={outerCn}>
      <div className='text-center italic'>Loading game data...</div>
    </div>
  }
  
  if (!battleResults || battleResults.length === 0) {
    return <div className={outerCn}>
      <div className='text-center italic'>{emptyTableMessage ?? 'Battles table is empty.'}</div>
    </div>
  }

  return (
    <div className={cn(className, "w-full text-left flex flex-col", outerCn)}>
      <div className='flex mb-2 justify-between items-center'>
        <div className='w-17'/>

        <span className='text-xl text-center'>
          {title ?? !opponent ? 'Counters table' : <>
            <Dialog>
              <DialogTrigger className='cursor-pointer'>
                <span className='underline decoration-dotted'>{opponent?.name}</span>
              </DialogTrigger>{' Counters'}
              <DialogContent>
                <DialogHeader>
                  <DialogTitle>{opponent.name} Lineup</DialogTitle>
                </DialogHeader>
                
                {Lineup.toArray(opponent.lineup).map((speciesIds, i) =>
                <OpponentCardRow
                  key={i}
                  className='bg-theme3'
                  speciesArr={speciesIds.map(id => clientData.species[id])}
                  slotNumber={i + 1}
                  asteriskCount={opponent.encounterSlots.includes(i + 1) ? (i + 1 === 2 ? 2 : 1) : undefined}
                />)}
              </DialogContent>
            </Dialog>
          </>}
        </span>

        <div className='flex gap-1'>
          <Dialog>
            <DialogTrigger className={cn(buttonVariants({ variant: 'blended' }), 'w-8 h-8')}>
              <Info className='size-5'/>
            </DialogTrigger>

            <DialogContent>
              <DialogHeader>
                <DialogTitle>Counters Info</DialogTitle>
              </DialogHeader>

              <p>
                Counters are ranked by <b>score</b>, which measures how quickly and reliably a counter can win. It is calculated as:
              </p>
              <div className='text-center'><CodeBlock>floor(10<sup>8</sup>/time * win%)</CodeBlock></div>
              <ul className='list-disc pl-5'>
                <li><CodeBlock>time</CodeBlock> is the average time, in seconds, for the counter to defeat any team the opponent can use (excluding losses).</li>
                <li><CodeBlock>win%</CodeBlock> is the percent of teams that the counter is able to successfully defeat.</li>
                <li><CodeBlock>10<sup>8</sup></CodeBlock> is an arbitrary constant used to make the final score easier to read.</li>
              </ul>
            </DialogContent>
          </Dialog>

          <Dialog>
            <DialogTrigger className={cn(buttonVariants({ variant: 'blended' }), 'w-8 h-8')} disabled={!useFilters}>
              <Settings2 className='size-5'/>
            </DialogTrigger>

            <DialogContent>
              <DialogHeader>
                <DialogTitle>Counters Configuration</DialogTitle>
              </DialogHeader>

                <div className='flex justify-between'>
                  Include Mega Pokémon <Switch checked={includeMega} onCheckedChange={v => setFilter('includeMega', v)}/>
                </div>
                <div className='flex justify-between'>
                  Include Shadow Pokémon <Switch checked={includeShadow} onCheckedChange={v => setFilter('includeShadow', v)}/>
                </div>
                <div className='flex justify-between'>
                  Include Legendary Pokémon <Switch checked={includeLegendary} onCheckedChange={v => setFilter('includeLegendary', v)}/>
                </div>
            </DialogContent>
          </Dialog>
        </div>
      </div>
      <div className={cn('grid border-b border-theme4 px-2 pb-2 -mb-px')} style={{ gridTemplateColumns: gridCols }}>
        {Object.entries(description).filter(e => e[1] && e[0] !== "dropdownIndicator").map(e =>
          <div
            key={e[0]}
            className={cn(e[0] === 'score' ? "font-bold" : "font-normal", e[0] === firstCol ? "col-span-2" : "")}
          >
            {colNames[e[0]]}
          </div>
        )}
      </div>
      <div className="flex flex-col">
        {filteredBattleResults.length === 0 && <div className='text-center italic text-sm py-4'>
          No results match the counters configuration.
        </div>}
        {battleResultsPage.map((br, i) => (
          <CountersTableRow
            key={i}
            description={description}
            battleResult={br}
            opponent={opponent}
            clientData={clientData}
          />
        ))}
      </div>
      <div className='gap-1 flex justify-end border-t border-theme4 pt-2 -mt-px'>
        <Button 
          className='w-8 h-8' 
          variant='blended' 
          onClick={() => page > 1 && setPage(page-1)}
          disabled={page <= 1}
        >
          <ChevronLeft className='size-5'/>
        </Button>

        <NumberSelect 
          value={page} 
          onValueChange={v => setPage(v ?? 1)} 
          min={1} 
          max={pageCount}
        />
        
        <Button 
          className='w-8 h-8' 
          variant='blended' 
          onClick={() => page < pageCount && setPage(page+1)}
          disabled={page >= pageCount}
        >
          <ChevronRight className='size-5'/>
        </Button>
      </div>
    </div>
  )
}

export default CountersTable