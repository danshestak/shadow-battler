import { cn } from '@/lib/utils'
import { CountersTableDescription, getGridColsByDesc } from './CountersTableDescription'
import CountersTableRow from './CountersTableRow'
import { BattleResult } from '@/types/BattleResult'
import { Opponent } from '@/types/Opponent'
import { ClientData } from '@/types/ClientData'

interface CountersTableProps {
  battleResults: BattleResult[]
  emptyTableMessage?: string,
  opponent: Opponent | null,
  clientData: ClientData | null,
  className?: string,
  description: CountersTableDescription,
}

const colNames: Record<string, string> = {
  species: "Species",
  moves: "Moveset",
  time: "Time",
  winpercent: "Win%",
  score: "Score",
}

const outerCn = 'rounded bg-theme3 border border-theme4 p-2 overflow-x-auto shadow-lg';

const CountersTable = ({ battleResults, emptyTableMessage, opponent, clientData, className, description }: CountersTableProps) => {
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
        {battleResults.map((br, i) => (
          <CountersTableRow
            key={i}
            description={description}
            battleResult={br}
            opponent={opponent}
            clientData={clientData}
          />
        ))}
      </div>
    </div>
  )
}

export default CountersTable