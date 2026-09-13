import { cn } from '@/lib/utils'
import { Move } from '@/types/Move'
import { Species } from '@/types/Species'
import { Type } from '@/types/Type'

interface MoveLabelProps {
    className?: string,
    move?: Move,
    species?: Species
}

const MoveLabel = ({ className, move, species }: MoveLabelProps) => {
    const color = move !== undefined ? Type.toHex(move.type ?? 'NONE') : null;
    return (
        <span 
        className={cn(className, "rounded border px-1 py-0.5", !move ? 'italic opacity-50 bg-theme3 border-theme4' : '')}
        style={{
        backgroundColor: color !== null ? `${color}80` : undefined,
        borderColor: color !== null ? `${color}80` : undefined,
        }}>
            {move?.name ?? 'No move'}
            {move !== undefined && species?.eliteMoves?.includes(move.moveId) && "*"}
            {move !== undefined && species?.legacyMoves?.includes(move.moveId) && <>&dagger;</>}
        </span>
    )
}

export default MoveLabel