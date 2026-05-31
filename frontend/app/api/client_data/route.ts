import { getMoves, getOpponents, getSpecies } from '@/lib/serverData';
import { NextResponse } from 'next/server';

export async function GET() {
    const opponents = await getOpponents();
    const species = await getSpecies();
    const moves = await getMoves();
    
    return NextResponse.json({ opponents: opponents, species: species, moves: moves });
}