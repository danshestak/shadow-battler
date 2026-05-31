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
import Link from 'next/link';
import CountersTable from '@/components/counters/CountersTable';
import CountersTableRow from '@/components/counters/CountersTableRow';
import { CountersTableDescription } from '@/components/counters/CountersTableDescription';
import { useClientData } from '@/lib/clientData';
import { Opponent } from '@/types/Opponent';

const countersTableDescription: CountersTableDescription = {
  dropdownIndicator: true,
  species: true,
  moves: true,
  time: true,
  winpercent: true,
  score: true
}

const CountersClientPage = () => {
  const { clientData, isError, isLoading } = useClientData()
  const router = useRouter();
  const params = useParams();

  if (isLoading || isError) {
    return (
      <div className={`text-center italic p-2 bg-theme3 rounded border transition shadow-lg ${isLoading ? 'border-theme4' : 'border-highlight'}`}>
        {isLoading ? 'Loading data...' : 'There was an error loading data.'}
      </div>
    )
  }

  const opponentSlug = Array.isArray(params.opponent)
    ? params.opponent[0]
    : undefined;

  const selectedOpponent = Object.values(clientData.opponents).find((o) => o.opponentId === opponentSlug);

  const handleValueChange = (name?: string | null) => {
    if (!name) return;

    const newOpponent = Object.values(clientData.opponents).find(o => o.name === name);
    if (newOpponent) {
      router.push(`/counters/${newOpponent.opponentId}`);
    }
  };

  return (
    <div>
      <Combobox
        items={Object.values(clientData.opponents)
          .sort(Opponent.compare)
          .map(o => {
            return { name: o.name, id: o.opponentId };
          })}
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
            <CountersTableRow description={countersTableDescription} />
            <CountersTableRow description={countersTableDescription} />
            <CountersTableRow description={countersTableDescription} />
          </CountersTable>
        }
      </div>
    </div>
  );
};

export default CountersClientPage;