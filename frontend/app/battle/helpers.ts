import { BattleResultRaw } from "@/types/BattleResult";
import { Creature, CreatureRaw, PlayerCreature } from "@/types/Creature";
import { Opponent } from "@/types/Opponent";

export const checkForIssues = (
    playerCreature: Creature,
    enemyCreature1: Creature,
    enemyCreature2: Creature,
    enemyCreature3: Creature,
    solveForMoveset: boolean,
    enemyMode: 'team' | 'lineup',
    opponent: Opponent | null,
    isReady: boolean
) => {
    if (!isReady) return ['Data is not loaded'];

    const currIssues = [];

    const checkCreatureForIssues = (c: Creature, name: string) => {
        if (c.species === undefined) {
            currIssues.push(`${name} is missing species`);
        } else {
            if (!(c.user === 'player' && solveForMoveset)) {
                if (c.fast === undefined) {
                    currIssues.push(`${name} is missing fast move`);
                }
                if (c.user === 'player') {
                    if (c.charged1 === undefined && (c as PlayerCreature).charged2 === undefined) {
                        currIssues.push(`${name} is missing charged move(s)`)
                    }
                } else {
                    if (c.charged1 === undefined) {
                        currIssues.push(`${name} is missing charged move`)
                    }
                }
            }
        }
    }

    checkCreatureForIssues(playerCreature, 'Player Pokémon');

    if (opponent === null) {
        currIssues.push('No opponent is selected');
    } else {
        if (enemyMode === 'team') {
            const enemyCreatures = [enemyCreature1, enemyCreature2, enemyCreature3] as const;
            for (let i = 0; i < enemyCreatures.length; i++) {
                checkCreatureForIssues(enemyCreatures[i], `Enemy Pokémon in slot ${i + 1}`)
            }
        }
    }

    return currIssues;
}

export const fetchBattle = async (
    urlBase: string,
    playerCreature: Creature,
    enemyCreature1: Creature,
    enemyCreature2: Creature,
    enemyCreature3: Creature,
    trainerLevel: number,
    solveForMoveset: boolean,
    enemyMode: 'team' | 'lineup',
    opponent: Opponent | null,
) => {
    const res = await fetch(urlBase + '/api/battle', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            opponentId: opponent?.opponentId,
            trainerLevel: trainerLevel,
            solveForMoveset: solveForMoveset,
            enemyMode: enemyMode,
            playerCreature: CreatureRaw.from(playerCreature),
            enemyCreature1: CreatureRaw.from(enemyCreature1),
            enemyCreature2: CreatureRaw.from(enemyCreature2),
            enemyCreature3: CreatureRaw.from(enemyCreature3)
        })
    });

    return !res.ok ? undefined : (await res.json()) as BattleResultRaw[];
}