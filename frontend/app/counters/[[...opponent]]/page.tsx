import CountersClientPage from './CountersClientPage';
import Link from 'next/link';
import CodeBlock from '@/components/CodeBlock';

const CountersPage = async () => {
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
      <CountersClientPage/>
    </div>
  )
};

export default CountersPage;