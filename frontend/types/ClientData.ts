import { Move } from "./Move"
import { Opponent } from "./Opponent"
import { Species } from "./Species"

export type ClientData = {
    opponents: Record<string, Opponent>,
    species: Record<string, Species>,
    moves: Record<string, Move>
}