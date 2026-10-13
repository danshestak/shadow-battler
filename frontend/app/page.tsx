import { SpritesheetData } from "@/components/sprite/SpritesheetData";
import { Button, buttonVariants } from "@/components/ui/button";
import { cn } from "cn";
import Link from "next/link";

const spriteScale = 2

export default function Home() {
  return (
    <main>
      <div className="relative isolate bg-theme3 border border-theme4 rounded shadow-xl min-h-48">
        <div
          className="absolute inset-0 z-0 overflow-hidden"
          style={{
            maskImage: "linear-gradient(to right, transparent 25%, white 200%)",
            WebkitMaskImage: "linear-gradient(to right, transparent 25%, white 200%)",
          }}
        >
          <div
            className="relative h-full w-full rounded"
            style={{
              left: "60%",
              width: SpritesheetData.width * spriteScale,
              height: SpritesheetData.width * spriteScale,
              backgroundImage: "url(/spritesheet.png)",
              backgroundRepeat: "no-repeat",
              backgroundSize: `${SpritesheetData.width * spriteScale}px ${SpritesheetData.height * spriteScale}px`,
              imageRendering: "pixelated",
              transform: "translate(-50%, -50%) rotate(15deg)",
              transformOrigin: "center",
            }}
          />
        </div>
        <div className="relative z-1 p-4 gap-4 flex flex-col justify-center min-h-48">
          <h1 className="text-2xl">
            Welcome to Shadow Battler!
          </h1>
          <div>
            Shadow Battler is the premier Team GO Rocket battle simulator for Pokémon GO. View each opponent&apos;s lineup, 
            compare the best Pokémon for defeating them, and even run custom battle simulations.
          </div>
        </div>
      </div>

      <h2 className="text-xl mt-4 mb-2">
        Pages
      </h2>
      <div className="flex flex-col md:flex-row gap-2">
        {
          [
            ["Opponents list", "View all opponents, their Pokémon lineups, and their reward encounters", "opponents"],
            ["Counters table", "Compare every Pokémon to find the best counters for defeating each opponent", "counters"],
            ["Battle simulator", "Run battle simulations using Pokémon with custom moves, levels, and IVs", "battle"]
          ].map((v, i) => <Link 
            key={i}
            href={"/"+v[2].toLowerCase()} 
            className={cn(buttonVariants(), 'p-4 min-w-0 flex-1 flex-col justify-start items-stretch text-start whitespace-normal')}
          >
            <h3 className="text-lg">{v[0]}</h3>
            <p className="text-sm">{v[1]}</p>
          </Link>
        )}
      </div>

      <h2 className="text-xl mt-4 mb-2">
        How does it work?
      </h2>
      <p className="mb-4">
        Shadow Battler&apos;s base battle solving algorithm tries every player decision to find the fastest win with fixed 
        teams and movesets. To solve opponents&apos; lineups of random teams and movesets, the results of each permutation are averaged out.
      </p>
      <p className="mb-4">
        Using this algorithm, simulations between each Pokémon and each opponent are saved to a database, where they can be
        sorted to find the best counters for each opponent.
      </p>
      <p>
        Want to learn more or contribute?
        Shadow Battler is proudly <Link href={"https://github.com/danshestak/shadow-battler"}><span className='text-highlight hover:underline'>open source</span></Link>!
      </p>
    </main>
  );
}