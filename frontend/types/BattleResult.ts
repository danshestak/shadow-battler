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
    playerLevel: number,
    trainerLevel: number,
    playerFastMoveId: string,
    playerChargedMove1Id: string,
    playerChargedMove2Id: string,
    playerSpeciesId: string,
    opponentId: string,
}

export type BattleResult = {
    id: number,
    timeElapsed: number,
    timeElapsedVariance: number,
    winPercent: number,
    hpPercent: number,
    score: number,
    playerLevel: number,
    trainerLevel: number,
    playerFastMove: Move,
    playerChargedMove1: Move,
    playerChargedMove2: Move,
    playerSpecies: Species,
    opponent: Opponent,
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
            playerFastMove: clientData.moves[raw.playerFastMoveId],
            playerChargedMove1: clientData.moves[raw.playerChargedMove1Id],
            playerChargedMove2: clientData.moves[raw.playerChargedMove2Id],
            playerSpecies: clientData.species[raw.playerSpeciesId],
            opponent: clientData.opponents[raw.opponentId],
            playerLevel: raw.playerLevel,
            trainerLevel: raw.trainerLevel
        }
    }
}