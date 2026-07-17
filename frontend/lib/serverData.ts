import 'server-only';
import { Opponent } from '@/types/Opponent';
import { Species } from '@/types/Species';
import { Move } from '@/types/Move';
import { getBackendUrl } from './env';

const get = async (slug: string) => {
  const res = await fetch(`${getBackendUrl()}/api/${slug}`, { next: { revalidate: 3600, tags: [slug] } });
  if (!res.ok) throw new Error(`Failed to fetch ${slug}`);
 
  return res.json();
}

export const getOpponents = (): Promise<Record<string, Opponent>> => get('opponents');
export const getSpecies = (): Promise<Record<string, Species>> => get('species');
export const getMoves = (): Promise<Record<string, Move>> => get('moves');