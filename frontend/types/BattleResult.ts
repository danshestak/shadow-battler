import { ClientData } from "./ClientData"
import { Move } from "./Move"
import { Opponent } from "./Opponent"
import { Species } from "./Species"

export type BattleResultRaw = {
    id: number,
    timeElapsed: number,
    timeElapsedVariance: number,
    winPercent: number,
    hpPercent: number,
    score: number,
    playerFastMove: string,
    playerChargedMove1: string,
    playerChargedMove2: string,
    playerSpecies: string,
    opponent: string,
    playerLevel: number,
    trainerLevel: number,
}

export type BattleResult = {
    id: number,
    timeElapsed: number,
    timeElapsedVariance: number,
    winPercent: number,
    hpPercent: number,
    score: number,
    playerFastMove: Move,
    playerChargedMove1: Move,
    playerChargedMove2: Move,
    playerSpecies: Species,
    opponent: Opponent,
    playerLevel: number,
    trainerLevel: number,
}

export const BattleResult = {
    fromRaw(raw: BattleResultRaw, clientData: ClientData): BattleResult {
        return {
            id: raw.id,
            timeElapsed: raw.timeElapsed,
            timeElapsedVariance: raw.timeElapsedVariance,
            winPercent: raw.winPercent,
            hpPercent: raw.hpPercent,
            score: raw.score,
            playerFastMove: clientData.moves[raw.playerFastMove],
            playerChargedMove1: clientData.moves[raw.playerChargedMove1],
            playerChargedMove2: clientData.moves[raw.playerChargedMove2],
            playerSpecies: clientData.species[raw.playerSpecies],
            opponent: clientData.opponents[raw.opponent],
            playerLevel: raw.playerLevel,
            trainerLevel: raw.trainerLevel
        }
    }
}