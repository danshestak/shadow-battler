import { ClientData } from '@/types/ClientData';
import 'client-only'
import useSWR from 'swr';

const fetcher = (url:string) => fetch(url).then((res) => res.json());

export function useClientData(): {
  clientData: ClientData,
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
    clientData: data as ClientData,
    isError: error,
    isLoading
  };
}