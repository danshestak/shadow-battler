import CountersClientPage from './CountersClientPage';
import Link from 'next/link';
import { getBackendUrl } from '@/lib/env';

export const metadata = {
  title: 'Counters' 
}

interface CountersPageProps {
  params: Promise<{ opponent?: string[] }>;
}

const CountersPage = async ({ params }: CountersPageProps) => {
  const resolvedParams = await params;
  const opponentSlug = resolvedParams && Array.isArray(resolvedParams.opponent) ? resolvedParams.opponent[0] : undefined;

  let initialBattleResults = undefined;
  if (opponentSlug) {
    try {
      const res = await fetch(`${getBackendUrl()}/api/counters/${opponentSlug}`);
      if (res.ok) {
        initialBattleResults = await res.json();
      }
    } catch {

    }
  }

  return (
    <div className="max-w-4xl m-auto">
      <h1 className="text-2xl mb-4">Counters</h1>

      <p className="mb-4">
        Select an opponent to view the Pokémon that are the top counters to their lineup. Click on a row to learn more about
        that Pokémon and its matchup.
        Looking for a list of <Link href={"/opponents"}><span className='text-highlight hover:underline'>all opponents</span></Link>?
      </p>

      <h1 className="text-xl mb-4">
        Select an opponent below:
      </h1>
      <CountersClientPage initialBattleResults={initialBattleResults}/>
    </div>
  )
};

export default CountersPage;