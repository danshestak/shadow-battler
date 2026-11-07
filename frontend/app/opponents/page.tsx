import OpponentCard from '@/components/opponent/OpponentCard'
import { getOpponents, getSpecies } from '@/lib/serverData'
import { Opponent } from '@/types/Opponent';

export const dynamic = 'force-dynamic';
export const metadata = {
  title: 'Opponents'
}

const OpponentsPage = async () => {
  const opponents = await getOpponents();
  const species = await getSpecies();

  return (
    <div className='m-auto'>
      <h1 className='text-2xl mb-4'>Opponents</h1>

      <p className='mb-4'>
        This is a list of every opponent in Pokémon GO, and the lineup of Pokémon they are currently using. When battling an opponent, they will use a team with one random Pokémon from each lineup slot.
      </p>

      <p className='mb-4'>
        Pokémon with <span className='text-highlight'>*</span> are encounters from defeating the opponent,
        and Pokémon with <span className='text-highlight'>**</span> have a 10% chance to be encounters instead.
      </p>

      <div className='grid grid-cols-1 sm:grid-cols-2 gap-4'>
        {Object.values(opponents)
          .sort(Opponent.compare)
          .map((opponent) => <OpponentCard
            key={opponent.opponentId}
            className='p-2 bg-theme3 border border-theme4 rounded shadow-lg'
            opponent={opponent}
            speciesData={species}
          />)}
      </div>
    </div>
  )
}

export default OpponentsPage