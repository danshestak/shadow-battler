import React, { useState } from 'react'
import Sprite from '../sprite/Sprite'
import MoveLabel from '../MoveLabel'
import { cn } from '@/lib/utils';
import Meter from '../Meter';
import { CountersTableDescription, getGridColsByDesc } from './CountersTableDescription';

interface CountersTableRowProps {
    description: CountersTableDescription
}

const CountersTableRow = ({ description }: CountersTableRowProps) => {
  const [isOpen, setIsOpen] = useState(false);

  const gridCols = getGridColsByDesc(description);
  const cellStyle = "pt-2 pb-2";

  return (
    <div className={cn('bg-theme3 rounded border border-transparent transition-colors duration-150', isOpen && 'bg-theme2 border-theme4 shadow-lg')}>
        <button onClick={() => setIsOpen(!isOpen)} className={cn('w-full text-left cursor-pointer grid items-center px-2')} style={{ gridTemplateColumns: gridCols, gap: '0.5rem' }}>
            {description.dropdownIndicator && <div className={cn("transition-transform text-xs text-right", isOpen && "rotate-90")}>&#128898;</div>}
            {description.species && <div className={cellStyle}>
                <div className='text-sm flex items-center gap-1'>
                    <Sprite species={{ id: "kyurem_white_shadow", dex: 0}}/>
                    <span className='tracking-tight'>Kyurem (White) (Shadow)</span>
                </div>
            </div>}
            {description.moves && <div className={cellStyle}>
                <div className='flex flex-wrap gap-0.5 items-start text-xs'>
                    <MoveLabel move={{ name: "Dragon Breath", type: "dragon" }}/>
                    <MoveLabel move={{ name: "Ice Burn", type: "ice" }} isLegacy={true}/>
                    <MoveLabel move={{ name: "Fusion Flare", type: "fire" }} isElite={true}/>
                </div>
            </div>}
            {description.time && <div className={cn(cellStyle, "text-sm tracking-tight")}>107s</div> }
            {description.winpercent && <div className={cn(cellStyle, "text-sm tracking-tight")}>99.7%</div> }
            {description.score && <div className={cn(cellStyle, "text-sm tracking-tight font-bold")}>2049</div> }
        </button>
        {isOpen && (
            <div className='p-1 border-t flex flex-col md:flex-row border-theme4 divide-y md:divide-x md:divide-y-0 divide-theme4 gap-y-2'>
                <div className='p-2 flex-1'>
                    <h3 className='mb-2 text-lg flex justify-between items-center'>
                        <span>Stats</span>
                        <span className='text-sm text-right'>@Level 40.0, 15/15/15 IVs</span>
                    </h3> 

                    <div className='text-xs mb-2'>
                        The stats this Pokémon has with the given Pokémon level and IVs. The Pokémon level can be changed through the Counters list settings.
                    </div>

                    <div className='text-xs grid grid-cols-[max-content_max-content_1fr] items-center gap-2 mb-2'>
                        <div className='text-sm'>CP:</div> 
                        <div className='col-span-2'>4605</div>

                        <div className='text-sm'>ATK:</div> 
                        <div>256.8</div> 
                        <Meter value={256.8} max={400} colorSensitivity={2} colorOffset={100} label="Attack" className='h-2.5'/>

                        <div className='text-sm'>DEF:</div> 
                        <div>156.4</div> 
                        <Meter value={156.4} max={400} colorSensitivity={2} colorOffset={100} label="Defense" className='h-2.5'/>

                        <div className='text-sm'>HP:</div> 
                        <div>205</div> 
                        <Meter value={205} max={400} colorSensitivity={2} colorOffset={100} label="HP" className='h-2.5'/>
                    </div>

                    <div className='text-xs text-center'>Shadow Pokémon have a 20% higher attack and 20% lower defense during damage calculations</div>
                </div>
                <div className='p-2 flex-1'>
                    <h3 className='mb-2 text-lg flex justify-between items-center'>
                        <span>Moves</span>
                    </h3> 

                    <div className='text-xs mb-2'>The moves this Pokémon can learn. Damage, DPT, and DPE factor Same Type Attack Bonus (STAB).</div>

                    <div className='text-xs grid grid-cols-[1fr_max-content_max-content_max-content] gap-2 mb-4'>
                        <div className='text-sm'>Fast Move</div>
                        <div className='text-sm text-center'>DPT</div>
                        <div className='text-sm text-center'>EPT</div>
                        <div className='text-sm text-center'>Turns</div>

                        <div><MoveLabel className='flex-none font-semibold' move={{ name: "Dragon Breath", type: "dragon" }}/></div>
                        <div className='text-center'>3.6</div>
                        <div className='text-center'>4</div>
                        <div className='text-center'>1</div>

                        <div><MoveLabel className='flex-none' move={{ name: "Ice Fang", type: "ice" }}/></div>
                        <div className='text-center'>4.8</div>
                        <div className='text-center'>3</div>
                        <div className='text-center'>2</div>

                        <div><MoveLabel className='flex-none' move={{ name: "Steel Wing", type: "steel" }}/></div>
                        <div className='text-center'>3.5</div>
                        <div className='text-center'>2.5</div>
                        <div className='text-center'>2</div>

                        <div className='text-sm mt-4'>Charged Move</div>
                        <div className='text-sm mt-4 text-center'>Dmg.</div>
                        <div className='text-sm mt-4 text-center'>Enrg.</div>
                        <div className='text-sm mt-4 text-center'>DPE</div>

                        <div><MoveLabel className='flex-none font-semibold' move={{ name: "Ice Burn", type: "ice" }} isLegacy={true}/></div>
                        <div className='text-center'>144</div>
                        <div className='text-center'>60</div>
                        <div className='text-center'>2.4</div>
                        <div className='text-left col-span-full -mt-1 italic'>30% chance -1 ATK enemy</div>

                        <div><MoveLabel className='flex-none font-semibold' move={{ name: "Fusion Flare", type: "fire" }} isElite={true}/></div>
                        <div className='text-center'>90</div>
                        <div className='text-center'>45</div>
                        <div className='text-center'>2</div>

                        <div><MoveLabel className='flex-none' move={{ name: "Dragon Pulse", type: "dragon" }}/></div>
                        <div className='text-center'>108</div>
                        <div className='text-center'>55</div>
                        <div className='text-center'>1.96</div>

                        <div><MoveLabel className='flex-none' move={{ name: "Focus Blast", type: "fighting" }}/></div>
                        <div className='text-center'>150</div>
                        <div className='text-center'>75</div>
                        <div className='text-center'>2</div>
                        
                        <div><MoveLabel className='flex-none' move={{ name: "Ancient Power", type: "rock" }}/></div>
                        <div className='text-center'>60</div>
                        <div className='text-center'>45</div>
                        <div className='text-center'>1.33</div>
                        <div className='text-left col-span-full -mt-1 italic'>10% chance +1 ATK, +1 DEF self</div>

                        <div><MoveLabel className='flex-none' move={{ name: "Blizzard", type: "ice" }}/></div>
                        <div className='text-center'>168</div>
                        <div className='text-center'>75</div>
                        <div className='text-center'>2.24</div>
                    </div>

                    <h4 className='mb-2 mt-4'>Legend</h4>
                    
                    <ul className='text-xs ml-3 list-disc'>
                        <li className='font-semibold'>Preferred move</li>
                        <li>Unobtainable by non-Elite TM*</li>
                        <li>Unobtainable by any TM<sup>&dagger;</sup></li>
                    </ul>
                </div>
                <div className='p-2 flex-1'>
                    <h3 className='text-lg mb-2'>Performance</h3>

                    <div className='text-xs mb-2'>
                        How this Pokémon performs against the selected opponent with the given Pokémon level, trainer level, and preferred moveset.
                    </div>

                    <div className='text-xs grid grid-cols-[2.5fr_1fr] items-center gap-2 mb-2'>
                        <div className='text-sm'>Time:</div> 
                        <div>107s</div>

                        <div className='text-sm'>Win%:</div> 
                        <div>99.7%</div> 

                        <div className='text-sm font-bold'>Score:</div> 
                        <div className='font-bold'>2049 &plusmn; 86</div> 
                    </div>

                    <h4 className='mb-2 mt-4'>Lineup performance</h4>

                    <div className='text-xs mb-2'>
                        Shows how performance changes against each Pokémon in the opponent&apos;s lineup.
                    </div>

                    <div className='text-xs grid grid-cols-[max-content_4fr_max-content_1fr] gap-2 items-center'>
                        <div className='text-sm'>Slot</div>
                        <div className='text-sm'>Species</div>
                        <div className='text-sm col-span-2'>&Delta;Score</div>

                        <div className='h-px col-span-full bg-theme4'/>

                        <div>1.</div>
                        <div>Ratata (Alolan) (Shadow)</div>
                        <div>+44.5%</div>
                        <Meter value={144.5} max={200} colorSensitivity={2} className='h-2.5'/>

                        <div>1.</div>
                        <div>Raticate (Alolan) (Shadow)</div>
                        <div>+7.3%</div>
                        <Meter value={107.3} max={200} colorSensitivity={2} className='h-2.5'/>

                        <div>1.</div>
                        <div>Kangaskhan (Shadow)</div>
                        <div>-65.2%</div>
                        <Meter value={34.8} max={200} colorSensitivity={2} className='h-2.5'/>

                        <div className='h-px col-span-full bg-theme4'/>

                        <div>2.</div>
                        <div>Ratata (Alolan) (Shadow)</div>
                        <div>+44.5%</div>
                        <Meter value={144.5} max={200} colorSensitivity={2} className='h-2.5'/>

                        <div>2.</div>
                        <div>Raticate (Alolan) (Shadow)</div>
                        <div>+7.3%</div>
                        <Meter value={107.3} max={200} colorSensitivity={2} className='h-2.5'/>

                        <div>2.</div>
                        <div>Kangaskhan (Shadow)</div>
                        <div>-65.2%</div>
                        <Meter value={34.8} max={200} colorSensitivity={2} className='h-2.5'/>

                        <div className='h-px col-span-full bg-theme4'/>

                        <div>3.</div>
                        <div>Ratata (Alolan) (Shadow)</div>
                        <div>+44.5%</div>
                        <Meter value={144.5} max={200} colorSensitivity={2} className='h-2.5'/>

                        <div>3.</div>
                        <div>Raticate (Alolan) (Shadow)</div>
                        <div>+7.3%</div>
                        <Meter value={107.3} max={200} colorSensitivity={2} className='h-2.5'/>

                        <div>3.</div>
                        <div>Kangaskhan (Shadow)</div>
                        <div>-65.2%</div>
                        <Meter value={34.8} max={200} colorSensitivity={2} className='h-2.5'/>
                    </div>
                </div>
            </div>
        )}
    </div>
  )
}

export default CountersTableRow