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
    const color = Type.toHex(move?.type ?? 'NONE');
    return (
        <span 
        className={cn(className, "rounded border p-0.5")}
        style={{
        backgroundColor: `${color}80`,
        borderColor: `${color}80`,
        }}>
            {move?.name ?? 'None'}
            {move !== undefined && species?.eliteMoves?.includes(move.moveId) && "*"}
            {move !== undefined && species?.legacyMoves?.includes(move.moveId) && <>&dagger;</>}
        </span>
    )
}

export default MoveLabel