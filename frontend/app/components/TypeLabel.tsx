import { CreatureType } from '@/types/CreatureType'
import { creatureTypeToColor } from '@/util/creatureTypeToColor';
import React from 'react'

interface TypeLabelProps {
  type:CreatureType
}

const TypeLabel = ({ type }:TypeLabelProps) => {
  const color = creatureTypeToColor(type);
  return (
    <span
    className="rounded-full pl-2 pr-2 pt-0.5 pb-0.5 uppercase font-semibold text-xs tracking-tight border"
    style={{
      backgroundColor: `${color}80`,
      borderColor: `${color}80`,
    }}>
      {type}
    </span>
  )
}

export default TypeLabel