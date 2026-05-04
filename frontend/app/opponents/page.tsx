import React from 'react'
import OpponentCard from '../components/opponent/OpponentCard'

const OpponentsPage = () => {
  return (
    <div className='max-w-3xl m-auto'>
      <h1 className='text-2xl mb-4'>Opponents</h1>

      <p className='mb-4'>
        This is a list of every opponent in the game and the lineup of Pokémon they are currently using. 
        You can click on any opponent to view which Pokémon are the most effective counters to their lineup!
      </p>

      <div className='grid grid-cols-1 sm:grid-cols-2 gap-4'>
        {[1, 2, 3, 4, 5].map((v, i) => <OpponentCard key={i}/>)}
      </div>
    </div>
  )
}

export default OpponentsPage