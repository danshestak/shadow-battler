import { getBackendUrl } from '@/lib/env';
import { NextRequest, NextResponse } from 'next/server';

export async function GET(request: NextRequest) {
    const opponentId = request.nextUrl.searchParams.get('opponentId');

    if (!opponentId) {
        return NextResponse.json({ error: 'Missing opponentId parameter' }, { status: 400 });
    }

    try {
        const res = await fetch(`${getBackendUrl()}/api/counters/${opponentId}`);
        
        if (!res.ok) {
            return NextResponse.json({ error: `Failed to fetch counters for ${opponentId}` }, { status: res.status });
        }

        const data = await res.json();
        return NextResponse.json(data);
        
    } catch {
        return NextResponse.json({ error: 'Failed to connect to backend' }, { status: 500 });
    }
}