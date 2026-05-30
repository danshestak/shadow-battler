import { getOpponents } from '@/lib/data';
import { Opponent } from '@/types/Opponent';
import React from 'react';
import CountersClientPage from './CountersClientPage';

const CountersPage = async () => {
  const opponentsData = await getOpponents();
  const opponentsOptions = Object.values(opponentsData)
    .sort(Opponent.compare)
    .map((o: Opponent) => ({
      name: o.name,
      value: o.opponentId,
    }));

  return <CountersClientPage opponentOptions={opponentsOptions} />;
};

export default CountersPage;