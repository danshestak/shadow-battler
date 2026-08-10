import { Move } from "./Move";
import { Species } from "./Species";
import { Stats3 } from "./Stats3"

export class Creature {
    #species: Species | undefined;
    level: number = 50;
    ivs: Stats3<number> = { atk: 15, def: 15, hp: 15 };
    fast: Move | undefined;
    charged1: Move | undefined;
    charged2: Move | undefined;
    
    set species(species: Species | undefined) {
        this.#species = species;
        this.fast = undefined;
        this.charged1 = undefined;
        this.charged2 = undefined;
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
        const clone = new Creature();
        clone.#species = this.species;
        clone.level = this.level;
        clone.ivs = { ...this.ivs };
        clone.fast = this.fast;
        clone.charged1 = this.charged1;
        clone.charged2 = this.charged2;
        return clone;
    }
}