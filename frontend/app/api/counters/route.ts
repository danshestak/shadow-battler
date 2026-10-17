import { getBackendUrl } from '@/lib/env';
import { errorResponse } from '@/lib/errorResponse';
import { NextRequest, NextResponse } from 'next/server';

export async function GET(request: NextRequest) {
    const opponentId = request.nextUrl.searchParams.get('opponentId');

    if (!opponentId) {
        return errorResponse(400, 'Missing opponentId parameter');
    }

    try {
        const res = await fetch(`${getBackendUrl()}/api/counters/${opponentId}`);
        if (!res.ok) return errorResponse(400, `Failed to fetch counters for ${opponentId}`);

        const data = await res.json();
        return NextResponse.json(data);
        
    } catch {
        return errorResponse(500, 'Failed to connect to backend');
    }
}