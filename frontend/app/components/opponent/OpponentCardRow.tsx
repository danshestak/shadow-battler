import React from 'react'
import Sprite from '../Sprite';

interface OpponentCardRowProps {
    speciesArr: string[];
}

const OpponentCardRow = ({ speciesArr }: OpponentCardRowProps) => {
  return (
    <div className='grid grid-cols-3 h-24 bg-theme2 border border-theme4 rounded'>
      {speciesArr.map((s, i) => <div key={i} className='m-auto'><Sprite id={s} alt={s}/></div>)}
    </div>
  )
}

export default OpponentCardRow