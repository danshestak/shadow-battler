import { Species } from "@/types/Species"
import Sprite from "./sprite/Sprite"
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "./ui/dialog"
import TypeLabel from "./TypeLabel"
import MoveLabel from "./MoveLabel"
import { Move } from "@/types/Move"

interface CreaturePanelProps {
    species: Species,
    moves: Record<string, Move>
}

const CreaturePanel = ({ species, moves }: CreaturePanelProps) => {
  return (
    <Dialog>
    <DialogTrigger>
      <div className='flex justify-between items-center p-2 text-sm text-start bg-theme1 border border-theme4 hover:border-highlight transition rounded shadow-lg cursor-pointer'>
        <div className='flex flex-col grow gap-1 items-stretch'>
          <h4 className='text-base'>
            {species.speciesName + ' '}
            <span className='text-nowrap text-sm tracking-tight px-1 rounded border border-theme4 bg-theme3'>
                {Species.getCp(Species.getStats(species, 50, { atk: 15, def: 15, hp: 15 }))} CP
            </span>
          </h4>
          <div className='flex gap-0.5 mb-2'>
            {species.types.map((t, i) => <TypeLabel type={t} key={i}/>)}
          </div>
          <div className='flex flex-wrap gap-0.5 items-start text-xs'>
            {[species.fastMoves[0], ...species.chargedMoves.slice(0, 2)].map((m, i) => <MoveLabel move={moves[m]} key={i}/>)}
          </div>
        </div>
        <Sprite species={species} scale={2}/>
      </div>
    </DialogTrigger>
    <DialogContent className="sm:max-w-sm">
      <DialogHeader>
        <DialogTitle>Edit Pokémon</DialogTitle>
      </DialogHeader>
      <DialogFooter>
        footer
      </DialogFooter>
    </DialogContent>
  </Dialog>

  )
}

export default CreaturePanel