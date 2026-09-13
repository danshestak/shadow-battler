import { Move } from "./Move";
import { OpponentTitle } from "./Opponent";
import { Species } from "./Species";
import { Stats3 } from "./Stats3"

export interface Creature {
    species: Species | undefined;
    readonly user: 'player' | 'opponent'
    ivs: Stats3<number>;
    readonly cp: number;
    readonly stats: Stats3<number>;
    fast: Move | undefined;
    charged1: Move | undefined;
    possibleFastMoves: string[];
    possibleChargedMoves: string[];
    clone(): this;
}

export class PlayerCreature implements Creature {
    #species: Species | undefined;
    readonly user = 'player';
    level: number = 50;
    ivs: Stats3<number> = { atk: 15, def: 15, hp: 15 };
    fast: Move | undefined;
    charged1: Move | undefined;
    charged2: Move | undefined;
    possibleFastMoves: string[] = [];
    possibleChargedMoves: string[] = [];
    
    set species(species: Species | undefined) {
        this.#species = species;

        this.possibleFastMoves = species?.fastMoves ?? [];
        this.possibleChargedMoves = species?.chargedMoves ?? [];

        if (!this.fast?.moveId || !this.possibleFastMoves.includes(this.fast?.moveId)) {
            this.fast = undefined;
        }
        if (!this.charged1?.moveId || !this.possibleChargedMoves.includes(this.charged1?.moveId)) {
            this.charged1 = undefined;
        }
        if (!this.charged2?.moveId || !this.possibleChargedMoves.includes(this.charged2?.moveId)) {
            this.charged2 = undefined;
        }
    }

    get species() {
        return this.#species;
    }

    get stats() {
        if (this.species === undefined) return { atk: 0, def: 0, hp: 0};
        return Species.getStats(this.species, this.level, this.ivs);
    }

    get cp() {
        return Species.getCp(this.stats);
    }

    clone() {
        const clone = new PlayerCreature();
        clone.species = this.species;
        clone.level = this.level;
        clone.ivs = { ...this.ivs };
        clone.fast = this.fast;
        clone.charged1 = this.charged1;
        clone.charged2 = this.charged2;
        return clone as this;
    }
}

export class OpponentCreature implements Creature {
    #species: Species | undefined;
    readonly user = 'opponent';
    readonly ivs: Stats3<number> = { atk: 15, def: 15, hp: 15 };
    trainerLevel: number;
    title: OpponentTitle; 
    fast: Move | undefined;
    charged1: Move | undefined;
    possibleFastMoves: string[] = [];
    possibleChargedMoves: string[] = [];

    constructor(trainerLevel: number, title: OpponentTitle) {
        this.trainerLevel = trainerLevel;
        this.title = title;
    }
    
    set species(species: Species | undefined) {
        this.#species = species;
        
        let unusableMoves: Set<string>;
        if (species !== undefined) {
            unusableMoves = new Set([...(species.eliteMoves ?? []), ...(species.legacyMoves ?? [])]);
        } else {
            unusableMoves = new Set();
        }

        this.possibleFastMoves = species?.fastMoves.filter(v => !unusableMoves.has(v)) ?? [];
        this.possibleChargedMoves = species?.chargedMoves.filter(v => !unusableMoves.has(v)) ?? [];

        if (!this.fast?.moveId || !this.possibleFastMoves.includes(this.fast?.moveId)) {
            this.fast = undefined;
        }
        if (!this.charged1?.moveId || !this.possibleChargedMoves.includes(this.charged1?.moveId)) {
            this.charged1 = undefined;
        }
    }

    get species() {
        return this.#species;
    }
 
    get stats() {
        if (this.species === undefined) return { atk: 0, def: 0, hp: 0};
        return Species.getOpponentStats(this.species, this.trainerLevel, this.title);
    }

    get cp() {
        return Species.getCp(this.stats);
    }

    clone() {
        const clone = new OpponentCreature(this.trainerLevel, this.title);
        clone.species = this.species;
        clone.trainerLevel = this.trainerLevel;
        clone.title = this.title;
        clone.fast = this.fast;
        clone.charged1 = this.charged1;
        return clone as this;
    }
}