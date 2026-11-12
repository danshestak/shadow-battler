import { useSyncExternalStore } from 'react';

export interface CountersFilters {
  includeMega: boolean,
  includeShadow: boolean,
  includeLegendary: boolean,
}

const storageKey = 'counters-filters';

const defaultFilters: CountersFilters = {
  includeMega: false,
  includeShadow: true,
  includeLegendary: true,
};

// Module-level so every CountersTable shares one value, even across remounts when navigating between opponents
let filters: CountersFilters | null = null;
const listeners = new Set<() => void>();

// Saved values layered over the defaults, skipping any that are missing or malformed
const readStored = (): CountersFilters => {
  const result = { ...defaultFilters };
  try {
    const stored = JSON.parse(localStorage.getItem(storageKey) ?? 'null');
    if (stored && typeof stored === 'object') {
      for (const key of Object.keys(defaultFilters) as (keyof CountersFilters)[]) {
        if (typeof stored[key] === 'boolean') result[key] = stored[key];
      }
    }
  } catch {
    // Storage unavailable or corrupt, keep defaults
  }
  return result;
};

const getSnapshot = (): CountersFilters => {
  if (filters === null) filters = readStored();
  return filters;
};

const getServerSnapshot = (): CountersFilters => defaultFilters;

const notify = () => listeners.forEach(l => l());

// Pick up changes saved by other tabs (key is null when storage is cleared)
if (typeof window !== 'undefined') {
  window.addEventListener('storage', e => {
    if (e.key !== null && e.key !== storageKey) return;
    filters = readStored();
    notify();
  });
}

const subscribe = (listener: () => void) => {
  listeners.add(listener);
  return () => { listeners.delete(listener); };
};

const setFilter = (key: keyof CountersFilters, value: boolean) => {
  filters = { ...getSnapshot(), [key]: value };
  try {
    localStorage.setItem(storageKey, JSON.stringify(filters));
  } catch {
    // Storage unavailable, the value still persists for this page session
  }
  notify();
};

/**
 * Counters table filter switches, shared between all counters tables and persisted in localStorage.
 */
export function useCountersFilters(): [CountersFilters, typeof setFilter] {
  return [useSyncExternalStore(subscribe, getSnapshot, getServerSnapshot), setFilter];
}
