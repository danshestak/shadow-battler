'use client';

import {
  Combobox,
  ComboboxContent,
  ComboboxEmpty,
  ComboboxInput,
  ComboboxItem,
  ComboboxList,
} from '@/components/ui/combobox';
import { useParams, useRouter } from 'next/navigation';
import useSWR from 'swr';
import Link from 'next/link';
import CountersTable from '@/components/counters/CountersTable';
import CountersTableRow from '@/components/counters/CountersTableRow';
import { CountersTableDescription } from '@/components/counters/CountersTableDescription';
import { useClientData } from '@/lib/clientData';
import { Opponent } from '@/types/Opponent';
import { BattleResult, BattleResultRaw } from '@/types/BattleResult';
import { useMediaQuery } from '@/hooks/useMediaQuery';

interface CountersClientPageProps {
  initialBattleResults: BattleResultRaw[] | null
}

const fetcher = (url: string) => fetch(url).then((res) => res.json());

const getLoadingOrErrorText = (isLoading: boolean, isError: boolean, isCountersLoading: boolean, isCountersError: boolean) => {
  if (isLoading || isCountersLoading) {
    return `Loading ${isLoading ? 'game data' : 'counters'}...`;
  } else if (isError || isCountersError) {
    return `There was an error loading ${isError ? 'game data' : 'counters'}.`;
  } else {
    return '';
  }
}

const CountersClientPage = ({ initialBattleResults }: CountersClientPageProps) => {
  const isSm = useMediaQuery(`(min-width: 640px)`);

  const { clientData, isError, isLoading } = useClientData()
  const router = useRouter();
  const params = useParams();

  const opponentSlug = Array.isArray(params.opponent) ? params.opponent[0] : undefined;
  const { 
    data: rawBattleResults, 
    error: isCountersError, 
    isLoading: isCountersLoading 
  } = useSWR<BattleResultRaw[]>(
    opponentSlug ? `/api/counters?opponentId=${opponentSlug}` : null, 
    fetcher,
    { fallbackData: initialBattleResults || undefined}
  );
  
  const isCountersPending = !rawBattleResults && !isCountersError && opponentSlug;

  if (isLoading || isError || isCountersError || isCountersPending) {
    return (
      <div className={`text-center italic p-2 bg-theme3 rounded border transition shadow-lg ${(isError || isCountersError) ? 'border-highlight' : 'border-theme4'}`}>
        {getLoadingOrErrorText(isLoading, isError, isCountersLoading, isCountersError)}
      </div>
    )
  }

  const selectedOpponent = Object.values(clientData.opponents).find((o) => o.opponentId === opponentSlug);

  const battleResults: BattleResult[] = rawBattleResults ? rawBattleResults.map(raw => BattleResult.fromRaw(raw, clientData)) : [];

  const countersTableDescription: CountersTableDescription = {
    dropdownIndicator: true,
    species: true,
    moves: true,
    time: isSm,
    winpercent: isSm,
    score: true
  };

  const handleValueChange = (name?: string | null) => {
    if (!name) return;

    const newOpponent = Object.values(clientData.opponents).find(o => o.name === name);
    if (newOpponent) {
      router.push(`/counters/${newOpponent.opponentId}`);
    }
  };

  return (
    <>
      <Combobox
        items={Object.values(clientData.opponents)
          .sort(Opponent.compare)
          .map(o => {return { name: o.name, id: o.opponentId };})}
        value={selectedOpponent?.name}
        onValueChange={handleValueChange}
      >
        <ComboboxInput placeholder="Search opponents..." className={'text-base shadow-lg mb-4'} />
        <ComboboxContent className={"shadow-lg"}>
          <ComboboxEmpty>No opponent found.</ComboboxEmpty>
          <ComboboxList>
            {(item) => (
              <ComboboxItem key={item.id} value={item.name}>
                {item.name}
              </ComboboxItem>
            )}
          </ComboboxList>
        </ComboboxContent>
      </Combobox>

      {selectedOpponent && <p className="mb-4">
        Viewing counters for {selectedOpponent.name}. View more details about them and
        their lineup <Link href={"/opponents"}><span className='text-highlight hover:underline'>here</span></Link>.
      </p>}

      <div className="rounded bg-theme3 border border-theme4 p-2 overflow-x-auto shadow-lg">
        {!selectedOpponent && <div className='text-center italic'>No opponent selected</div>}

        {selectedOpponent &&
          <CountersTable description={countersTableDescription}>
            {battleResults.map((br, i) => (
              <CountersTableRow key={i} 
              description={countersTableDescription}
              battleResult={br}
              clientData={clientData}/>
            ))}
          </CountersTable>
        }
      </div>
    </>
  );
};

export default CountersClientPage;