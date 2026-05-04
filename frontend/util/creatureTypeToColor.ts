import { CreatureType } from "@/types/CreatureType";

const colors: Record<CreatureType, string> = {
    normal: "#9FA19F",
    fire: "#E62829",
    water: "#2980EF",
    grass: "#3FA129",
    electric: "#FAC000",
    ice: "#3DCEF3",
    fighting: "#FF8000",
    poison: "#9141CB",
    ground: "#915121",
    flying: "#81B9EF",
    psychic: "#EF4179",
    bug: "#91A119",
    rock: "#AFA981",
    ghost: "#704170",
    dragon: "#5060E1",
    dark: "#624D4E",
    steel: "#60A1B8",
    fairy: "#EF70EF",
    none: "#5A5A5A"
}

export const creatureTypeToColor = (type:CreatureType) => colors[type]