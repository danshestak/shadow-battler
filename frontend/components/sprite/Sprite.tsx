import React from 'react';
import { Species } from '@/types/Species';
import { IconIndexes } from './IconIndexes';
import { cn } from '@/lib/utils';

interface SpriteProps {
    species: Species;
    className?: string;
    scale?: number;
}

const width = 40;
const height = 30;
const spritesheetWidth = 480;
const spritesheetHeight = 4110;

const Sprite = ({ species, className, scale = 1 }: SpriteProps) => {
  let id = species.speciesId.toLowerCase();
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

  const num = IconIndexes[id] ?? species.dex;

	const top = Math.floor(num / 12) * height;
	const left = (num % 12) * width;
  
  const style: React.CSSProperties = {
    width: `${width * scale}px`,
    height: `${height * scale}px`,
    imageRendering: 'pixelated',
    backgroundRepeat: 'no-repeat',
  };

  const mainSpritePosition = `-${left * scale}px -${top * scale}px`;
  const spritesheetSize = `${spritesheetWidth * scale}px ${spritesheetHeight * scale}px`;

  if (shadow) {
    style.backgroundImage = `url(/spritesheet.png), url(/shadowsprite.png)`;
    style.backgroundPosition = `${mainSpritePosition}, center`;
    style.backgroundSize = `${spritesheetSize}, ${width * scale}px ${height * scale}px`;
  } else {
    style.backgroundImage = 'url(/spritesheet.png)';
    style.backgroundPosition = mainSpritePosition;
    style.backgroundSize = spritesheetSize;
  }

  return (
    <div
      role="img"
      aria-label={`${species.speciesId}_sprite`}
      className={cn(className, "flex-none")}
      style={style}
    />
  )
}

export default Sprite