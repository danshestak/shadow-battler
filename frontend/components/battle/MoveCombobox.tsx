import { cn } from "@/lib/utils";
import { Combobox, ComboboxContent, ComboboxEmpty, ComboboxInput, ComboboxItem, ComboboxList } from "../ui/combobox";
import { Move } from "@/types/Move";
import { Species } from "@/types/Species";

interface MoveComboboxProps {
  className?: string,
  disabled?: boolean,
  selectableMoveIds: string[],
  movesData: Record<string, Move>,
  userSpecies?: Species,
  value?: string,
  onValueChange: (moveId: string | null) => void
}

const MoveCombobox = ({ className, disabled, selectableMoveIds, movesData, userSpecies, value, onValueChange }: MoveComboboxProps) => {
  const displayValue = (value && movesData[value]) ? Move.getNameWithSymbols(movesData[value], userSpecies) : null;

  return (
    <Combobox
      items={Object.values(selectableMoveIds)
        .map(id => {
          const m = movesData[id];

          return { 
            id: m.moveId, 
            name: Move.getNameWithSymbols(m, userSpecies)
          };
        })
        .sort((a, b) => a.name.localeCompare(b.name))}
      value={displayValue}
      onValueChange={(selectedName) => {
        if (!selectedName) return onValueChange(null);

        const lastChar = selectedName.charAt(selectedName.length-1)
        if (lastChar === Move.eliteSymbol || lastChar === Move.legacySymbol) {
          selectedName = selectedName.slice(0, -1);
        }

        const selectedMove = Object.values(movesData).find((m) => m.name === selectedName);
        onValueChange(selectedMove ? selectedMove.moveId : null);
      }}
      autoHighlight={true}
    >
      <ComboboxInput 
      disabled={disabled}
      placeholder="Select move..." 
      className={cn('text-sm shadow-lg p-0 w-45', className)} 
      />
        <ComboboxContent className={"shadow-lg"}>
          <ComboboxEmpty>No moves found.</ComboboxEmpty>
          <ComboboxList>
          {(item) => (
            <ComboboxItem key={item.id} value={item.name}>
              {item.name}
            </ComboboxItem>
          )}
        </ComboboxList>
      </ComboboxContent>
    </Combobox>
  )
}

export default MoveCombobox