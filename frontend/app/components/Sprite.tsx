import React from 'react';
import Image from 'next/image';

interface SpriteProps {
    id: string;
    alt: string;
    type?: 'front' | 'back';
    shiny?: boolean;
    width?: number;
    height?: number;
}

//placeholder
const Sprite = ({ id, alt, type = 'front', shiny = false, width = 96, height = 96 }: SpriteProps) => {
  return (
    <img 
      style={{ imageRendering: 'crisp-edges' }}
      src={`https://img.pokemondb.net/sprites/black-white/normal/${id}.png`}
      alt={alt}
    />
  )
}

export default Sprite