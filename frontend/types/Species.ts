import { Stats3 } from "./Stats3"

export type Species = {
    dex: number,
    speciesName: string,
    speciesId: string,
    baseStats: Stats3<number>,
    types: string[],
    buddyDistance: number,
    released: boolean,
    family: {
      id: string,
      parent: string,
      evolutions: string[]
    },
    shadow: boolean,
    thirdMoveEnabled: boolean,
    fastMoves: string[],
    chargedMoves: string[],
    eliteMoves: string[],
    legacyMoves: string[],
    tags: string[],
    thirdMoveCost: number
  }