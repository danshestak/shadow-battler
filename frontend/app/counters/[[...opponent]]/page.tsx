'use client';

import {
  Combobox,
  ComboboxContent,
  ComboboxEmpty,
  ComboboxInput,
  ComboboxItem,
  ComboboxList,
} from '@/components/ui/combobox';
import React from 'react';
import { useParams, useRouter } from 'next/navigation';
import Link from 'next/link';
import CodeBlock from '@/components/CodeBlock';
import CountersTable from '@/components/counters/CountersTable';
import CountersTableRow from '@/components/counters/CountersTableRow';
import { CountersTableDescription } from '@/components/counters/CountersTableDescription';

const opponents = [
  { name: 'Normal-type Grunt', value: 'normal_type_grunt' },
  { name: 'Fire-type Grunt', value: 'fire_type_grunt' },
  { name: 'Dragon-type Grunt', value: 'dragon_type_grunt' },
];

const countersTableDescription: CountersTableDescription = {
  dropdownIndicator: true,
  species: true,
  moves: true,
  time: true,
  winpercent: true,
  score: true
}

const CountersPage = () => {
  const router = useRouter();
  const params = useParams();
  const opponentSlug = Array.isArray(params.opponent)
    ? params.opponent[0]
    : undefined;

  const selectedOpponent = opponents.find((o) => o.value === opponentSlug);

  const handleValueChange = (name?: string | null) => {
    if (!name) {
      router.push('/counters');
      return;
    }

    const opponent = opponents.find((o) => o.name === name);
    if (opponent) {
      router.push(`/counters/${opponent.value}`);
    }
  };

  return (
    <div className="max-w-4xl m-auto">
      <h1 className="text-2xl mb-4">Counters</h1>

      <p className="mb-4">
        Select an opponent to view the Pokémon that are the top counters to their own lineup of Pokémon. 
        Looking for a list of <Link href={"/opponents"}><span className='text-highlight hover:underline'>all opponents</span></Link> and 
        their lineups?
      </p>

      <p>
        The main metric used to measure performance and rank counters is score, which is calculated
        as <CodeBlock>floor(10<sup>8</sup>/time * win%)</CodeBlock>. Here&apos;s an explanation of each variable:
      </p>
      <ul className='mb-4 list-disc pl-5'>
        <li><CodeBlock>10<sup>8</sup></CodeBlock> is an arbitrary constant used to make the final score easier to read.</li>
        <li><CodeBlock>time</CodeBlock> is the average time, in seconds, for the counter to defeat any team the opponent can use.</li>
        <li><CodeBlock>win%</CodeBlock> is the percent of teams that the counter is able to successfully defeat.</li>
      </ul>

      <h1 className="text-xl mb-4 pt-4 border-t border-theme4">
        Select an opponent below:
      </h1>

      <Combobox
        items={opponents}
        value={selectedOpponent?.name}
        onValueChange={handleValueChange}
      >
        <ComboboxInput placeholder="Search opponents..." className={'text-base shadow mb-4'}/>
        <ComboboxContent>
          <ComboboxEmpty>No opponent found.</ComboboxEmpty>
          <ComboboxList>
            {(item) => (
              <ComboboxItem key={item.value} value={item.name}>
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

      <div className="rounded bg-theme3 border border-theme4 p-2 shadow overflow-x-auto">
        {!selectedOpponent && <div className='text-center italic'>No opponent selected</div>}

        {selectedOpponent &&
          <>
            <CountersTable description={countersTableDescription}>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
              <CountersTableRow description={countersTableDescription}/>
            </CountersTable>
          </>
        }
      </div>
    </div>
  );
};

export default CountersPage;