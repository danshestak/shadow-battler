'use client';

import { Select, SelectTrigger, SelectValue, SelectContent, SelectGroup, SelectItem } from "../ui/select";

interface NumberComboboxProps {
  min: number,
  max: number,
  step?: number
  value?: number,
  onValueChange: (value: number | null) => void
}

const NumberCombobox = ({ min, max, step = 1, value, onValueChange }: NumberComboboxProps) => {
  const numbers = [];
  for (let i = min; i <= max; i += step) {
    numbers.push({ label: i, value: i });
  }

  return (
    <Select 
      items={numbers}
      value={value}
      onValueChange={onValueChange}
    >
      <SelectTrigger>
        <SelectValue placeholder='--'/>
      </SelectTrigger>
      <SelectContent>
        <SelectGroup>
          {numbers.map((item) => (
            <SelectItem key={item.value} value={item.value}>
              {item.label}
            </SelectItem>
          ))}
        </SelectGroup>
      </SelectContent>
    </Select>
    // <Combobox 
    //   items={numbers}
    //   value={value}
    //   onValueChange={onValueChange}
    //   autoHighlight={true}
    // >
    //   <ComboboxInput 
    //   disabled={disabled}
    //   placeholder="--" 
    //   className={cn('text-sm shadow-lg p-0 w-15', className)} />
    //     <ComboboxContent className={"shadow-lg"}>
    //       <ComboboxList>
    //       {(item) => (
    //         <ComboboxItem key={item} value={item}>
    //           {item}
    //         </ComboboxItem>
    //       )}
    //     </ComboboxList>
    //   </ComboboxContent>
    // </Combobox>
  )
}

export default NumberCombobox