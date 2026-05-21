import React from 'react'
import OpponentCardRow from './OpponentCardRow'
import Link from 'next/link'
import TypeLabel from '../TypeLabel'

const OpponentCard = () => {
  return (
    <div className='p-2 bg-theme3 border border-theme4 rounded shadow'>
        <div className='flex justify-between items-center border-b border-theme4 pb-2'>
            <div>
                <div className='text-xl'>Leader Cliff</div>
                <div className='text-sm italic'>Team GO Rocket Leader</div>
            </div>

            <TypeLabel type={'normal'}/>
        </div>

        <div className='grid grid-rows-3 pt-2 gap-2 border-b border-theme4 pb-2'>
            {[
                [{ id: "snorlax_shadow", dex: 143}],
                [{ id: "gardevoir_shadow", dex: 282}, { id: "golurk_shadow", dex: 623}, { id: "weezing_galarian_shadow", dex: 0}],
                [{ id: "tyranitar_shadow", dex: 248}, { id: "camerupt_shadow", dex: 323}, { id: "gallade_shadow", dex: 475}]
            ].map((arr, i) => <OpponentCardRow key={i} speciesArr={arr} asteriskCount={((i===0) ? 1 : undefined)}/>)}
        </div>

        <div className='pt-2 flex justify-end'>
            <Link href="/counters/normal_type_grunt" className='
            bg-highlight p-2 rounded border border-transparent shadow
            hover:bg-theme2 hover:border-highlight transition
            active:border-text'
            >View counters &#9656;
            </Link>
        </div>
    </div>
  )
}

export default OpponentCard