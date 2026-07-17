import React, { useState } from 'react'
import Sprite from '../sprite/Sprite'
import MoveLabel from '../MoveLabel'
import { cn } from '@/lib/utils';
import Meter from '../Meter';
import { CountersTableDescription, getGridColsByDesc } from './CountersTableDescription';
import { Species } from '@/types/Species';
import { BattleResult } from '@/types/BattleResult';
import { ClientData } from '@/types/ClientData';

interface CountersTableRowProps {
    description: CountersTableDescription,
    battleResult: BattleResult
    clientData: ClientData
}

const roundNumber = (n: number) => Math.round(n * 100)/100;
const cellStyle = 'py-2 pr-1';
const openCategoryStyle = 'md:p-4 md:pt-0 p-2 pt-0 flex-1'

const CountersTableRow = ({ description, battleResult, clientData }: CountersTableRowProps) => {
    const [isOpen, setIsOpen] = useState(false);

    const gridCols = getGridColsByDesc(description);
    const stats = Species.getStats(battleResult.playerSpecies, battleResult.playerLevel, {atk: 15, def: 15, hp: 15})

    return (
        <div className={cn('bg-theme3 rounded border border-transparent transition-colors duration-150', isOpen && 'bg-theme2 border-theme4 shadow-lg')}>
            <button onClick={() => setIsOpen(!isOpen)} className={cn('w-full text-left cursor-pointer grid items-center gap-1 px-2')} style={{ gridTemplateColumns: gridCols }}>
                {description.dropdownIndicator && <div className={cn("transition-transform text-xs text-right", isOpen && "rotate-90")}>&#128898;</div>}
                {description.species && <div className={cellStyle}>
                    <div className='text-sm flex items-center gap-1'>
                        <Sprite species={battleResult.playerSpecies} />
                        <span className='tracking-tight'>{battleResult.playerSpecies.speciesName}</span>
                    </div>
                </div>}
                {description.moves && <div className={cellStyle}>
                    <div className='flex flex-wrap gap-0.5 items-start text-xs'>
                        {(['playerFastMove', 'playerChargedMove1', 'playerChargedMove2'] as const).map((s, i) => (
                            <MoveLabel
                            key={i}
                            move={battleResult[s]}
                            species={battleResult.playerSpecies}/>
                        ))}
                    </div>
                </div>}
                {description.time &&
                    <div className={cn(cellStyle, "text-sm tracking-tight", battleResult.winPercent === 0 ? 'italic' : '')}>
                        {battleResult.winPercent !== 0 ? `${(battleResult.timeElapsed/1000).toFixed(1)}s` : 'DNF'}
                    </div>
                }
                {description.winpercent && 
                    <div className={cn(cellStyle, "text-sm tracking-tight")}>
                        {(battleResult.winPercent * 100).toFixed(1)}%
                    </div>
                }
                {description.score && 
                    <div className={cn(cellStyle, "text-sm tracking-tight font-bold")}>
                        {battleResult.score}
                    </div>
                }
            </button>
            {isOpen && (
                <div className='pt-2 md:pb-2 px-2 md:px-0 border-t flex flex-col md:flex-row border-theme4 divide-y md:divide-x md:divide-y-0 divide-theme4 gap-y-2'>
                    <div className={openCategoryStyle}>
                        <h3 className='mb-2 text-lg flex justify-between items-center'>
                            <span>Stats</span>
                            <span className='text-sm text-right'>@Level {battleResult.playerLevel.toFixed(1)}, 15/15/15 IVs</span>
                        </h3>

                        <div className='text-xs mb-2'>
                            The stats this species has with the given Pokémon level and IVs. The Pokémon level can be changed through the Counters list settings.
                        </div>

                        <div className='text-xs grid grid-cols-[max-content_max-content_1fr] items-center gap-2 mb-2 p-1'>
                            <div className='text-sm'>CP:</div>
                            <div className='col-span-2'>{Species.getCp(stats)}</div>

                            <div className='text-sm'>ATK:</div>
                            <div>{stats.atk.toFixed(1)}</div>
                            <Meter value={stats.atk} max={400} colorSensitivity={2} colorOffset={100} label="Attack" className='h-2.5' />

                            <div className='text-sm'>DEF:</div>
                            <div>{stats.def.toFixed(1)}</div>
                            <Meter value={stats.def} max={400} colorSensitivity={2} colorOffset={100} label="Defense" className='h-2.5' />

                            <div className='text-sm'>HP:</div>
                            <div>{stats.hp}</div>
                            <Meter value={stats.hp} max={400} colorSensitivity={2} colorOffset={100} label="HP" className='h-2.5' />
                        </div>

                        <div className='text-xs text-center'>Shadow Pokémon have a 20% higher attack and 20% lower defense during damage calculations</div>
                    </div>
                    <div className={openCategoryStyle}>
                        <h3 className='mb-2 text-lg flex justify-between items-center'>
                            <span>Moves</span>
                        </h3>

                        <div className='text-xs mb-2'>The moves this species can learn. Damage, DPT, and DPE factor Same Type Attack Bonus (STAB).</div>

                        <div className='text-xs grid grid-cols-[4fr_1fr_1fr_1fr] gap-2 mb-4 p-1'>
                            <div className='text-sm'>Fast Move</div>
                            <div className='text-sm text-center'>DPT</div>
                            <div className='text-sm text-center'>EPT</div>
                            <div className='text-sm text-center'>Turns</div>

                            {battleResult.playerSpecies.fastMoves
                                .map(m => clientData.moves[m])
                                .map((m, i) => (
                                    <React.Fragment key={i}>
                                        <div><MoveLabel
                                            className={`flex-none ${(m.moveId === battleResult.playerFastMove.moveId) ? 'font-bold' : ''}`}
                                            move={m}
                                            species={battleResult.playerSpecies} />
                                        </div>
                                        <div className='text-center'>
                                            {roundNumber(m.power / m.turns * (Species.givesStab(battleResult.playerSpecies, m) ? 1.2 : 1))}
                                        </div>
                                        <div className='text-center'>
                                            {roundNumber(m.energyGain / m.turns)}
                                        </div>
                                        <div className='text-center'>
                                            {m.turns}
                                        </div>
                                    </React.Fragment>
                                ))
                            }

                            <div className='text-sm mt-4'>Charged Move</div>
                            <div className='text-sm mt-4 text-center'>Dmg.</div>
                            <div className='text-sm mt-4 text-center'>Enrg.</div>
                            <div className='text-sm mt-4 text-center'>DPE</div>

                            {battleResult.playerSpecies.chargedMoves
                                .map(m => clientData.moves[m])
                                .map((m, i) => (
                                    <React.Fragment key={i}>
                                        <div><MoveLabel
                                            className={`flex-none ${(m.moveId === battleResult.playerChargedMove1.moveId || m.moveId === battleResult.playerChargedMove2.moveId) ? 'font-bold' : ''}`}
                                            move={m}
                                            species={battleResult.playerSpecies} />
                                        </div>
                                        <div className='text-center'>
                                            {roundNumber(m.power * (Species.givesStab(battleResult.playerSpecies, m) ? 1.2 : 1))}
                                        </div>
                                        <div className='text-center'>
                                            {m.energy}
                                            </div>
                                        <div className='text-center'>
                                            {roundNumber(m.power / m.energy * (Species.givesStab(battleResult.playerSpecies, m) ? 1.2 : 1))}
                                        </div>
                                    </React.Fragment>
                                ))
                            }
                        </div>

                        <ul className='text-xs text-right mt-6'>
                            <li className='font-bold'>Preferred move</li>
                            <li>Unobtainable by non-Elite TM*</li>
                            <li>Unobtainable by any TM<sup>&dagger;</sup></li>
                        </ul>
                    </div>
                    <div className={openCategoryStyle}>
                        <h3 className='text-lg mb-2'>Performance</h3>

                        <div className='text-xs mb-2'>
                            How a Pokémon of this species performs against the selected opponent with the given Pokémon level, trainer level, and preferred moveset.
                        </div>

                        <div className='text-xs grid grid-cols-[2fr_1fr] items-center gap-2 mb-2 p-1'>
                            <div className='text-sm'>Time:</div>
                            <div>
                                {(battleResult.timeElapsed/1000).toFixed(1)}s
                            </div>

                            <div className='text-sm'>Win%:</div>
                            <div>
                                {(battleResult.winPercent * 100).toFixed(1)}%
                            </div>

                            <div className='text-sm'>HP%:</div>
                            <div>
                                {(battleResult.hpPercent * 100).toFixed(1)}%
                            </div>

                            <div className='text-sm font-bold'>Score:</div>
                            <div className='font-bold'>
                                {battleResult.score} &plusmn; {Math.round(battleResult.timeElapsedVariance)}
                            </div>
                        </div>

                        {/* <h4 className='mb-2 mt-4'>Lineup performance</h4>

                        <div className='text-xs mb-2'>
                            Shows how performance changes against each Pokémon in the opponent&apos;s lineup.
                        </div>

                        <div className='text-xs grid grid-cols-[max-content_3fr_max-content_1fr] gap-2 items-center'>
                            <div className='text-sm'>Slot</div>
                            <div className='text-sm'>Species</div>
                            <div className='text-sm col-span-2'>&Delta;Score</div>

                            <div>1.</div>
                            <div>Ratata (Alolan) (Shadow)</div>
                            <div>+44.5%</div>
                            <Meter value={144.5} max={200} colorSensitivity={2} className='h-2.5' />

                            <div></div>
                            <div>Raticate (Alolan) (Shadow)</div>
                            <div>+7.3%</div>
                            <Meter value={107.3} max={200} colorSensitivity={2} className='h-2.5' />

                            <div></div>
                            <div>Kangaskhan (Shadow)</div>
                            <div>-65.2%</div>
                            <Meter value={34.8} max={200} colorSensitivity={2} className='h-2.5' />

                            <div>2.</div>
                            <div>Ratata (Alolan) (Shadow)</div>
                            <div>+44.5%</div>
                            <Meter value={144.5} max={200} colorSensitivity={2} className='h-2.5' />

                            <div></div>
                            <div>Raticate (Alolan) (Shadow)</div>
                            <div>+7.3%</div>
                            <Meter value={107.3} max={200} colorSensitivity={2} className='h-2.5' />

                            <div></div>
                            <div>Kangaskhan (Shadow)</div>
                            <div>-65.2%</div>
                            <Meter value={34.8} max={200} colorSensitivity={2} className='h-2.5' />

                            <div>3.</div>
                            <div>Ratata (Alolan) (Shadow)</div>
                            <div>+44.5%</div>
                            <Meter value={144.5} max={200} colorSensitivity={2} className='h-2.5' />

                            <div></div>
                            <div>Raticate (Alolan) (Shadow)</div>
                            <div>+7.3%</div>
                            <Meter value={107.3} max={200} colorSensitivity={2} className='h-2.5' />

                            <div></div>
                            <div>Kangaskhan (Shadow)</div>
                            <div>-65.2%</div>
                            <Meter value={34.8} max={200} colorSensitivity={2} className='h-2.5' />
                        </div> */}
                    </div>
                </div>
            )}
        </div>
    )
}

export default CountersTableRow