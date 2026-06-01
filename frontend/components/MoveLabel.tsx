import { cn } from '@/lib/utils'
import { Move } from '@/types/Move'
import { Species } from '@/types/Species'
import { Type } from '@/types/Type'
import React from 'react'

interface MoveLabelProps {
    className?: string,
    move: Move,
    species?: Species
}

const MoveLabel = ({ className, move, species }: MoveLabelProps) => {
    const color = Type.toHex(move.type);
    return (
        <span 
        className={cn(className, "rounded border p-0.5")}
        style={{
        backgroundColor: `${color}80`,
        borderColor: `${color}80`,
        }}>
            {move.name}
            {species?.eliteMoves?.includes(move.moveId) && "*"}
            {species?.legacyMoves?.includes(move.moveId) && <sup>&#8224;</sup>}
        </span>
    )
}

export default MoveLabel