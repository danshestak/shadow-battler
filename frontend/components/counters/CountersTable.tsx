import { cn } from '@/lib/utils'
import React, { ReactNode } from 'react'
import { CountersTableDescription, getGridColsByDesc } from './CountersTableDescription'

interface CountersTableProps {
    children?: ReactNode,
    className?: string,
    description: CountersTableDescription
}

const colNames: Record<string, string> = {
    species: "Species",
    moves: "Moveset",
    time: "Time",
    winpercent: "Win%",
    score: "Score",
}

const CountersTable = ({ children, className, description }: CountersTableProps) => {
    const gridCols = getGridColsByDesc(description);

    let firstCol: string | undefined = undefined;
    if (description.dropdownIndicator)  {
        for (const e of Object.entries(description)) {
            if (e[1] && e[0] != "dropdownIndicator") {
                firstCol = e[0];
                break;
            }
        }
    }
    
    return (
        <div className={cn(className, "w-full text-left flex flex-col")}>
            <div className={cn('grid border-b border-theme4 px-2 pb-2 mb-1')} style={{ gridTemplateColumns: gridCols }}>
                {Object.entries(description).filter(e => e[1] && e[0] !== "dropdownIndicator").map(e => 
                    <div key={e[0]} className={cn(e[0] === 'score' ? "font-bold" : "font-normal", e[0] === firstCol ? "col-span-2" : "")}>
                        {colNames[e[0]]}
                    </div>
                )}
            </div>
            <div className="flex flex-col">
                {children}
            </div>
        </div>
    )
}

export default CountersTable