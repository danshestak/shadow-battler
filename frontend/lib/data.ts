import 'server-only';
import { Opponent } from '@/types/Opponent';
import { Species } from '@/types/Species';

export async function getOpponents(): Promise<Record<string, Opponent>> {
  const backendUrl = process.env.SPRING_BOOT_API_URL;

  if (!backendUrl) {
    throw new Error('SPRING_BOOT_API_URL is not defined. Please check your .env.local file.');
  }
  
  const res = await fetch(`${backendUrl}/api/opponents`, { 
    next: { revalidate: 3600, tags: ['opponents'] } 
  });

  if (!res.ok) {
    throw new Error('Failed to fetch opponents');
  }
 
  return res.json();
}

export async function getSpecies(): Promise<Record<string, Species>> {
  const backendUrl = process.env.SPRING_BOOT_API_URL;

  if (!backendUrl) {
    throw new Error('SPRING_BOOT_API_URL is not defined. Please check your .env.local file.');
  }

  const res = await fetch(`${backendUrl}/api/species`, {
    next: { revalidate: 3600, tags: ['species'] }
  });

  if (!res.ok) {
    throw new Error('Failed to fetch species');
  }

  return res.json();
}