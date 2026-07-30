import CreaturePanel from '@/components/CreaturePanel';
import { getMoves, getSpecies } from '@/lib/serverData';

const BattlePage = async () => {
  const species = await getSpecies();
  const moves = await getMoves();

  return (
    <div className="max-w-3xl m-auto">
      <h1 className="text-2xl mb-4">Battle</h1>

      <p className="mb-4">
        Simulate battles between a Pokémon and an opponent team or lineup.
      </p>

      <div className='grid grid-cols-1 sm:grid-cols-2 gap-4'>
        <CreaturePanel species={species['charizard_mega_y']} moves={moves}/>
      </div>
    </div>
  )
}

export default BattlePage