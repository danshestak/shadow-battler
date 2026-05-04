import React from 'react'
import OpponentCardRow from './OpponentCardRow'
import Link from 'next/link'
import TypeLabel from '../TypeLabel'

const OpponentCard = () => {
  return (
    <div className='p-2 bg-theme3 border border-theme4 rounded shadow'>
        <div className='flex justify-between items-center border-b border-theme4 pb-2'>
            <span className='text-xl tracking-tight'>Normal-type Grunt</span>

            <TypeLabel type={'normal'}/>
        </div>

        <div className='grid grid-rows-3 pt-2 gap-2 border-b border-theme4 pb-2'>
            {[
                ["rattata"],
                ["rattata", "raticate", "pidgeotto"],
                ["pidgeotto", "raticate", "pidgeot"]
            ].map((arr, i) => <OpponentCardRow key={i} speciesArr={arr}/>)}
        </div>

        <div className='pt-2 flex justify-end'>
            <Link href="/" className='
            bg-highlight p-2 rounded border border-transparent shadow
            hover:bg-theme2 hover:border-highlight transition
            active:text-highlight'
            >View counters &#9656;
            </Link>
        </div>
    </div>
  )
}

export default OpponentCard