import { Species } from "./Species"
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

export const Move = {
    eliteSymbol: '*',
    legacySymbol: '†',

    getSymbol(move: Move, species: Species) {
        if (species.eliteMoves?.includes(move.moveId)) {
            return this.eliteSymbol;
        } else if (species.legacyMoves?.includes(move.moveId)) {
            return this.legacySymbol;
        }
        return '';
    },

    getNameWithSymbols(move: Move, species?: Species) {
        return species ? move.name + this.getSymbol(move, species) : move.name;
    }
}