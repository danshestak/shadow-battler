import { ClientData } from '@/types/ClientData';
import 'client-only'
import useSWR, { SWRConfiguration } from 'swr';

const fetcher = (url:string) => fetch(url).then((res) => res.json());

function createLocalStorageProvider() {
  if (typeof window === 'undefined') return () => new Map();

  const map = new Map(JSON.parse(localStorage.getItem('client-data-cache') || '[]'));

  window.addEventListener('beforeunload', () => {
    const appCache = JSON.stringify(Array.from(map.entries()));
    localStorage.setItem('client-data-cache', appCache);
  });

  return () => map;
}

const localStorageProvider = createLocalStorageProvider();

export function useClientData(): {
  clientData: ClientData,
  isError: boolean,
  isLoading: boolean
} {
  const swrOptions: SWRConfiguration = {
    revalidateOnFocus: false,
    revalidateOnReconnect: false,
    dedupingInterval: 24 * 60 * 60 * 1000,
    provider: localStorageProvider
  };

  const { data, error, isLoading } = useSWR('/api/client_data', fetcher, swrOptions);

  return {
    clientData: data as ClientData,
    isError: error,
    isLoading
  };
}