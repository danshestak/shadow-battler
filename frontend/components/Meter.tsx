import { cn } from '@/lib/utils'
import { CreatureType } from '@/types/Type';
import { creatureTypeToColor } from '@/util/creatureTypeToColor';
import React from 'react'

interface MeterProps {
    className?: string,
    value: number,
    min?: number,
    max: number,
    colorSensitivity?: number,
    colorOffset?: number,
    label?: string,
}

const colors = ["fire", "electric", "grass"].map(v => creatureTypeToColor(v as CreatureType))

function lerpColor(color1: string, color2: string, factor: number): string {
    const hexToRgb = (hex: string) => {
        const r = parseInt(hex.slice(1, 3), 16);
        const g = parseInt(hex.slice(3, 5), 16);
        const b = parseInt(hex.slice(5, 7), 16);
        return [r, g, b];
    };

    const rgbToHex = (r: number, g: number, b: number) => {
        return "#" +
        (Math.round(r)).toString(16).padStart(2, '0') +
        (Math.round(g)).toString(16).padStart(2, '0') +
        (Math.round(b)).toString(16).padStart(2, '0');
    };

    const rgb1 = hexToRgb(color1);
    const rgb2 = hexToRgb(color2);

    const r = rgb1[0] + factor * (rgb2[0] - rgb1[0]);
    const g = rgb1[1] + factor * (rgb2[1] - rgb1[1]);
    const b = rgb1[2] + factor * (rgb2[2] - rgb1[2]);

    return rgbToHex(r, g, b);
}

const Meter = ({ className, value, min = 0, max, colorSensitivity = 1, colorOffset = 0, label = 'Meter' }: MeterProps) => {
    value = Math.max(min, Math.min(max, value));
    const normalizedValue = (value - min) / (max - min);
    const colorOffsetPercent = colorOffset / (max - min)
    const colorValue = Math.max(0, Math.min(1, (normalizedValue*colorSensitivity) - (0.5*colorSensitivity) + 0.5 + colorOffsetPercent));

    let interpolatedColor: string;
    if (colorValue <= 0.5) {
        interpolatedColor = lerpColor(colors[0], colors[1], colorValue*2);
    } else {
        interpolatedColor = lerpColor(colors[1], colors[2], colorValue*2 - 1);
    }

    const percentage = normalizedValue * 100;

    return (
        <div
            className={cn(className, "bg-theme1 border border-theme4 rounded overflow-clip")}
            role="meter"
            aria-valuenow={value}
            aria-valuemin={min}
            aria-valuemax={max}
            aria-valuetext={`${label}: ${percentage.toFixed(0)}%`}
            aria-label={label}
        >
            <div
                className='h-full border rounded'
                style={{
                    width: `${percentage}%`,
                    backgroundColor: interpolatedColor+"c0",
                    borderColor: interpolatedColor+"c0"
                }}
            />
        </div>
    )
}

export default Meter