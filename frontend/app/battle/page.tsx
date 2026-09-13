import BattleClientPage from './BattleClientPage';

const BattlePage = async () => {
  return (
    <div className="max-w-3xl m-auto">
      <h1 className="text-2xl mb-4">Battle</h1>

      <p className="mb-4">
        Simulate battles between a Pokémon and an opponent team or lineup.
      </p>

      <BattleClientPage/>
    </div>
  )
}

export default BattlePage