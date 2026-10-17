import { getBackendUrl } from '@/lib/env';
import { errorResponse } from '@/lib/errorResponse';
import { NextRequest, NextResponse } from 'next/server';

export async function POST(request: NextRequest) {
    let body;
    try {
        body = await request.json();
    } catch {
        return errorResponse(400, 'Invalid body');
    }
    
    try {
        const res = await fetch(`${getBackendUrl()}/api/battle`, { 
            method: 'POST', 
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                opponentId: body.opponentId,
                trainerLevel: body.trainerLevel,
                solveForMoveset: body.solveForMoveset,
                enemyMode: body.enemyMode,
                playerCreature: body.playerCreature,
                enemyCreature1: body.enemyCreature1,
                enemyCreature2: body.enemyCreature2,
                enemyCreature3: body.enemyCreature3
            })
        });
        
        const data = await res.json();
        if (!res.ok) return errorResponse(res.status, data.message);

        return NextResponse.json(data);
    } catch {
        return errorResponse(500, 'Failed to connect to backend');
    }
}