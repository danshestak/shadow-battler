import { getCountersConfig, getCachedResponse } from '@/lib/serverData';
import { ClientData } from '@/types/ClientData';
import { NextResponse } from 'next/server';

const newETag = (values: Array<string | null>) => 'client-data-' + Buffer.from(values.map(etag => etag ?? '').join('|')).toString('base64url');
const matchesETag = (header: string | null, etag: string) => header?.split(',').some(value => value.trim() === etag) ?? false;

export async function GET(request: Request) {
    const [opponentsResponse, speciesResponse, movesResponse] = await Promise.all([
        getCachedResponse('opponents'),
        getCachedResponse('species'),
        getCachedResponse('moves')
    ]);
    const countersConfig = await getCountersConfig();

    const etag = newETag([ opponentsResponse.etag, speciesResponse.etag, movesResponse.etag]);
    const headers = { 'Cache-Control': 'no-cache', ETag: etag };

    if (matchesETag(request.headers.get('if-none-match'), etag)) {
        return new NextResponse(null, { status: 304, headers });
    }

    return NextResponse.json({
        opponents: opponentsResponse.data,
        species: speciesResponse.data,
        moves: movesResponse.data,
        countersConfig: countersConfig
    } as ClientData, { headers });
}