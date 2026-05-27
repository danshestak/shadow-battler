import { Type } from '@/types/Type'
import React from 'react'

interface TypeLabelProps {
  type:Type
}

const TypeLabel = ({ type }:TypeLabelProps) => {
  const hex = Type.toHex(type);
  return (
    <span
    className="rounded-full pl-2 pr-2 pt-0.5 pb-0.5 uppercase font-semibold text-xs tracking-tight border"
    style={{
      backgroundColor: `${hex}80`,
      borderColor: `${hex}80`,
    }}>
      {type}
    </span>
  )
}

export default TypeLabel