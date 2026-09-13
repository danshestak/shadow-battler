import { cn } from '@/lib/utils'

interface CombatPowerLabelProps {
  cp: number,
  className?: string
};

const CombatPowerLabel = ({ cp, className }: CombatPowerLabelProps) => {
  return (
    <span className={cn('text-nowrap text-sm tracking-tight px-1 rounded border border-theme4 bg-theme3 shadow-lg', className)}>
      {cp} CP
    </span>
  )
}

export default CombatPowerLabel