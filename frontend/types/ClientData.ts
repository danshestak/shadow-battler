import { Move } from "./Move"
import { Opponent } from "./Opponent"
import { Species } from "./Species"

export type CountersConfig = {
    playerCreatureLevels: number[],
    trainerLevels: number[]
}

export type ClientData = {
    opponents: Record<string, Opponent>,
    species: Record<string, Species>,
    moves: Record<string, Move>
    countersConfig: CountersConfig
}