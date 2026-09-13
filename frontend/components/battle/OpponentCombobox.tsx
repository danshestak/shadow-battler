import { Combobox, ComboboxContent, ComboboxEmpty, ComboboxInput, ComboboxItem, ComboboxList } from "../ui/combobox";
import { Opponent } from "@/types/Opponent";

interface OpponentComboboxProps {
  className?: string,
  disabled?: boolean,
  opponentsData: Record<string, Opponent>,
  value: string | null,
  onValueChange: (moveId: string | null) => void
}

const OpponentCombobox = ({ className, disabled, opponentsData, value, onValueChange }: OpponentComboboxProps) => {
  return (
    <Combobox
      items={Object.values(opponentsData)
        .sort(Opponent.compare)
        .map(o => {return { name: o.name, id: o.opponentId };})}
      value={value}
      onValueChange={(selectedName) => {
        if (!selectedName) return onValueChange(null)
        const selectedOpponent = Object.values(opponentsData).find((s) => s.name === selectedName);
        onValueChange(selectedOpponent ? selectedOpponent.opponentId : null);
      }}
      autoHighlight={true}
    >
      <ComboboxInput 
      disabled={disabled}
      placeholder="Select opponent..." 
      className={className} 
      />
        <ComboboxContent>
          <ComboboxEmpty>No opponents found.</ComboboxEmpty>
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

export default OpponentCombobox