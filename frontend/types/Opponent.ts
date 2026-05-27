import { Type } from "./Type"
import { Lineup } from "./Lineup"

export type Opponent = {
    opponentId: string,
    name: string,
    title: OpponentTitle,
    limit: number,
    lineup: Lineup<string>,
    encounterSlots: number[],
    specialtyType: Type | null
}

export type OpponentTitle = "ROCKET_GRUNT" | "ROCKET_LEADER" | "ROCKET_BOSS"

const full: Record<OpponentTitle, string> = {
    ROCKET_GRUNT: "Team GO Rocket Grunt",
    ROCKET_LEADER: "Team GO Rocket Leader",
    ROCKET_BOSS: "Team GO Rocket Boss"
}
const order: Record<OpponentTitle, number> = {
    ROCKET_BOSS: 0,
    ROCKET_LEADER: 1,
    ROCKET_GRUNT: 2
}
export const OpponentTitle = {
    toFull(title: OpponentTitle): string {
        return full[title];
    },
    
    toOrder(title: OpponentTitle): number {
        return order[title];
    }
}