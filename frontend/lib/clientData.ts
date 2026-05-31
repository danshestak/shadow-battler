import { Move } from '@/types/Move';
import { Opponent } from '@/types/Opponent';
import { Species } from '@/types/Species';
import 'client-only'
import useSWR from 'swr';

const fetcher = (url:string) => fetch(url).then((res) => res.json());

export function useClientData(): {
  clientData: {
    opponents: Record<string, Opponent>,
    species: Record<string, Species>
    moves: Record<string, Move>
  },
  isError: boolean,
  isLoading: boolean
} {
  const { data, error, isLoading } = useSWR(
    '/api/client_data', fetcher, {
    revalidateOnFocus: false,
    revalidateOnReconnect: false,
    dedupingInterval: 24 * 60 * 60 * 1000,
  });

  return {
    clientData: data,
    isError: error,
    isLoading
  };
}