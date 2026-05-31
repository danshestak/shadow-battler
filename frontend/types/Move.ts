import { Stats3 } from "./Stats3"
import { Type } from "./Type"

export type Move = {
    moveId: string,
    name: string,
    abbreviation: string,
    type: Type,
    power: number,
    energy: number,
    energyGain: number,
    buffsSelf: Stats3<number>,
    buffsOpponent: Stats3<number>,
    buffApplyChance: number,
    archetype: string,
    turns: number
}