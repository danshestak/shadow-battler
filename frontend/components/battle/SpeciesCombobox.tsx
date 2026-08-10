import { Species } from "@/types/Species";
import { Combobox, ComboboxContent, ComboboxEmpty, ComboboxInput, ComboboxItem, ComboboxList } from "../ui/combobox";

interface SpeciesComboboxProps {
  speciesData: Record<string, Species>,
  value?: string,
  onValueChange: (speciesId: string | null) => void
}

const SpeciesCombobox = ({ speciesData, value, onValueChange }: SpeciesComboboxProps) => {
  const displayValue = value ? speciesData[value]?.speciesName : null;

  return (
    <Combobox 
      items={Object.values(speciesData)
        .map(s => {return { name: s.speciesName, id: s.speciesId };})
        .sort((a, b) => a.name.localeCompare(b.name))}
      value={displayValue} 
      onValueChange={(selectedName) => {
        if (!selectedName) return onValueChange(null)
        const selectedSpecies = Object.values(speciesData).find((s) => s.speciesName === selectedName);
        onValueChange(selectedSpecies ? selectedSpecies.speciesId : null);
      }}
      autoHighlight={true}
      >
      <ComboboxInput placeholder="Select species..." className={'text-sm shadow-lg p-0 w-60'} />
        <ComboboxContent className={"shadow-lg"}>
          <ComboboxEmpty>No Pokémon found.</ComboboxEmpty>
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

export default SpeciesCombobox