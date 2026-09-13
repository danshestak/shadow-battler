'use client';

import { Select, SelectTrigger, SelectValue, SelectContent, SelectGroup, SelectItem } from "../ui/select";

interface NumberSelectProps {
  min: number,
  max: number,
  step?: number
  value?: number,
  onValueChange: (value: number | null) => void,
  disabled?: boolean
}

interface NumberSelectPlaceholderProps {
  value?: number
}

const NumberSelect = ({ min, max, step = 1, value, onValueChange, disabled }: NumberSelectProps) => {
  const numbers = [];
  for (let i = min; i <= max; i += step) {
    numbers.push({ label: i, value: i });
  }

  return (
    <Select 
      items={numbers}
      value={value}
      onValueChange={onValueChange}
      disabled={disabled}
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
  )
}

export const NumberSelectPlaceholder = ({ value }: NumberSelectPlaceholderProps) => {
  return (
    <Select disabled={true} value={value}>
      <SelectTrigger>
        <SelectValue/>
      </SelectTrigger>
    </Select>
  )
}

export default NumberSelect