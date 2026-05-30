import { cn } from '@/lib/utils'
import { Move } from '@/types/Move'
import { Type } from '@/types/Type'
import React from 'react'

interface MoveLabelProps {
    className?: string,
    move: Move,
    isElite?: boolean,
    isLegacy?: boolean
}

const MoveLabel = ({ className, move, isElite, isLegacy }: MoveLabelProps) => {
    const color = Type.toHex(move.type);
    return (
        <span 
        className={cn(className, "rounded border p-0.5")}
        style={{
        backgroundColor: `${color}80`,
        borderColor: `${color}80`,
        }}>
            {move.name}
            {isElite && "*"}
            {isLegacy && <sup>&#8224;</sup>}
        </span>
    )
}

export default MoveLabel