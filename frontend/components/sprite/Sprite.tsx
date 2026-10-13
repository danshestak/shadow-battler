import React from 'react';
import { Species } from '@/types/Species';
import { SpritesheetData } from './SpritesheetData';
import { cn } from '@/lib/utils';

interface SpriteProps {
    species?: Species;
    className?: string;
    scale?: number;
}

const Sprite = ({ species, className, scale = 1 }: SpriteProps) => {
  let id = species?.speciesId.toLowerCase() ?? 'none';
  let shadow = false;
  
  const shadowSubstring = "_shadow";
  if (id.endsWith(shadowSubstring)) {
    shadow = true;
    id = id.slice(0, id.length - shadowSubstring.length);
  }

  const regionals = [
    ["_alolan", "alola"], 
    ["_galarian", "galar"], 
    ["_paldean", "paldea"], 
    ["_hisuian", "hisui"]
  ];
  for (const regional of regionals) {
    if (!id.endsWith(regional[0])) continue;
    id = id.slice(0, id.length - regional[0].length) + regional[1];
  }

  id = id.replaceAll("_", "");

  const num = SpritesheetData.iconIndexes[id] ?? species?.dex ?? 0;

	const top = Math.floor(num / 12) * SpritesheetData.cellHeight;
	const left = (num % 12) * SpritesheetData.cellWidth;
  
  const style: React.CSSProperties = {
    width: `${SpritesheetData.cellWidth * scale}px`,
    height: `${SpritesheetData.cellHeight * scale}px`,
    imageRendering: 'pixelated',
    backgroundRepeat: 'no-repeat',
  };

  const mainSpritePosition = `-${left * scale}px -${top * scale}px`;
  const spritesheetSize = `${SpritesheetData.width * scale}px ${SpritesheetData.height * scale}px`;

  if (shadow) {
    style.backgroundImage = `url(/spritesheet.png), url(/shadowsprite.png)`;
    style.backgroundPosition = `${mainSpritePosition}, center`;
    style.backgroundSize = `${spritesheetSize}, ${SpritesheetData.cellWidth * scale}px ${SpritesheetData.cellHeight * scale}px`;
  } else {
    style.backgroundImage = 'url(/spritesheet.png)';
    style.backgroundPosition = mainSpritePosition;
    style.backgroundSize = spritesheetSize;
  }

  return (
    <div
      role="img"
      aria-label={`${id}_sprite`}
      className={cn(className, "flex-none")}
      style={style}
    />
  )
}

export default Sprite