import { Move } from "./Move";
import { OpponentTitle } from "./Opponent";
import { Species } from "./Species";
import { Stats3 } from "./Stats3"

const parseNum = (s: string, min: number, max: number, step: number = 1) => {
  const x = Number(s);
  return (!Number.isFinite(x) || x < min || x > max || x % step !== 0) ? null : x;
}

export type Creature = PlayerCreature | OpponentCreature;

export type PlayerCreature = {
  species: Species | undefined,
  user: 'player',
  level: number,
  ivs: Stats3<number>,
  fast: Move | undefined,
  charged1: Move | undefined,
  charged2: Move | undefined,
  possibleFastMoves: string[],
  possibleChargedMoves: string[]
}

export type OpponentCreature = {
  species: Species | undefined,
  user: 'opponent',
  ivs: Stats3<number>,
  trainerLevel: number,
  title: OpponentTitle,
  fast: Move | undefined,
  charged1: Move | undefined,
  possibleFastMoves: string[],
  possibleChargedMoves: string[],
}

export const PlayerCreature = {
  new(): PlayerCreature {
    return {
      species: undefined,
      user: 'player',
      level: 50,
      ivs: { atk: 15, def: 15, hp: 15 },
      fast: undefined,
      charged1: undefined,
      charged2: undefined,
      possibleFastMoves: [],
      possibleChargedMoves: []
    }
  },

  statefulSetSpecies(c: PlayerCreature, species: Species | undefined) {
    c.species = species;

    c.possibleFastMoves = species?.fastMoves ?? [];
    c.possibleChargedMoves = species?.chargedMoves ?? [];

    if (!c.fast?.moveId || !c.possibleFastMoves.includes(c.fast?.moveId)) c.fast = undefined;
    if (!c.charged1?.moveId || !c.possibleChargedMoves.includes(c.charged1?.moveId)) c.charged1 = undefined;
    if (!c.charged2?.moveId || !c.possibleChargedMoves.includes(c.charged2?.moveId)) c.charged2 = undefined;
  },

  getStats(c: PlayerCreature) {
    if (c.species === undefined) return { atk: 0, def: 0, hp: 0};
    return Species.getStats(c.species, c.level, c.ivs);
  },
  
  toSearchParams(c: PlayerCreature) {
    return [
      c.species?.speciesId ?? '_',
      c.level, 
      c.ivs.atk,
      c.ivs.def,
      c.ivs.hp,
      c.fast?.moveId ?? '_',
      c.charged1?.moveId ?? '_',
      c.charged2?.moveId ?? '_'
    ].join(',').toLowerCase();
  },

  fromSearchParams(speciesData: Record<string, Species>, movesData: Record<string, Move>,  params: string) {
    const c = PlayerCreature.new();

    const arr = params.split(',');
    if (arr.length !== 8) return c;

    const species = speciesData[arr[0].trim().toLowerCase()];
    if (species) PlayerCreature.statefulSetSpecies(c, species);

    const level = parseNum(arr[1], 1, 55, 0.5);
    if (level !== null) c.level = level;

    const ivs = arr.slice(2, 5).map(s => parseNum(s, 0, 15, 1) ?? 15);
    c.ivs = { atk: ivs[0], def: ivs[1], hp: ivs[2] };

    const fast = movesData[arr[5].trim().toUpperCase()];
    if (fast && c.possibleFastMoves.includes(fast.moveId)) c.fast = fast;

    const charged1 = movesData[arr[6].trim().toUpperCase()];
    if (charged1 && c.possibleChargedMoves.includes(charged1.moveId)) c.charged1 = charged1;

    const charged2 = movesData[arr[7].trim().toUpperCase()];
    if (charged2 && c.possibleChargedMoves.includes(charged2.moveId) && charged1.moveId !== charged2.moveId) c.charged2 = charged2;
    
    return c;
  }
}

export const OpponentCreature = {
  new(trainerLevel: number, title: OpponentTitle = 'ROCKET_GRUNT'): OpponentCreature {
    return {
      species: undefined,
      user: 'opponent',
      ivs: { atk: 15, def: 15, hp: 15 },
      trainerLevel: trainerLevel,
      title: title,
      fast: undefined,
      charged1: undefined,
      possibleFastMoves: [],
      possibleChargedMoves: []
    }
  },
  
  statefulSetSpecies(c: OpponentCreature, species: Species | undefined) {
    c.species = species;
    
    let unusableMoves: Set<string>;
    if (species !== undefined) {
      unusableMoves = new Set([...(species.eliteMoves ?? []), ...(species.legacyMoves ?? [])]);
    } else {
      unusableMoves = new Set();
    }

    c.possibleFastMoves = species?.fastMoves.filter(v => !unusableMoves.has(v)) ?? [];
    c.possibleChargedMoves = species?.chargedMoves.filter(v => !unusableMoves.has(v)) ?? [];

    if (!c.fast?.moveId || !c.possibleFastMoves.includes(c.fast?.moveId)) c.fast = undefined;
    if (!c.charged1?.moveId || !c.possibleChargedMoves.includes(c.charged1?.moveId)) c.charged1 = undefined;
  },

  getStats(c: OpponentCreature) {
    if (c.species === undefined) return { atk: 0, def: 0, hp: 0};
    return Species.getOpponentStats(c.species, c.trainerLevel, c.title);
  },

  toSearchParams(c: OpponentCreature) {
    return [
      c.species ?? '_',
      c.fast ?? '_',
      c.charged1 ?? '_'
    ].join(',');
  },

  fromSearchParams(speciesData: Record<string, Species>, movesData: Record<string, Move>, params: string, trainerLevel = 70, title: OpponentTitle = 'ROCKET_GRUNT') {
    const c = OpponentCreature.new(trainerLevel, title);

    const arr = params.split(',');
    if (arr.length !== 3) return c;

    const species = speciesData[arr[0].trim().toLowerCase()];
    if (species) OpponentCreature.statefulSetSpecies(c, species);

    const fast = movesData[arr[1].trim().toUpperCase()];
    if (fast && c.possibleFastMoves.includes(fast.moveId)) c.fast = fast;

    const charged1 = movesData[arr[2].trim().toUpperCase()];
    if (charged1 && c.possibleChargedMoves.includes(charged1.moveId)) c.charged1 = charged1;

    return c;
  }
}