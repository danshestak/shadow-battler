import { Type } from '@/types/Type'
import React from 'react'

interface TypeLabelProps {
  type:Type
}

const TypeLabel = ({ type }:TypeLabelProps) => {
  const hex = Type.toHex(type);
  return (
    <span
      className="rounded-full px-1.5 py-[0.25] uppercase font-semibold text-xs tracking-tight border"
      style={{
        backgroundColor: `${hex}80`,
        borderColor: `${hex}80`,
      }}>
        {type}
    </span>
  )
}

export default TypeLabel