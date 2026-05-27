export const typesArr = [
    "NORMAL", "FIRE", "WATER", "GRASS", "ELECTRIC", "ICE", "FIGHTING", "POISON", "GROUND", 
    "FLYING", "PSYCHIC", "BUG", "ROCK", "GHOST", "DRAGON", "DARK", "STEEL", "FAIRY", "NONE"
];

export type Type = "NORMAL" | "FIRE" | "WATER" | "GRASS" | "ELECTRIC" | "ICE" | "FIGHTING" | "POISON" | "GROUND" | 
    "FLYING" | "PSYCHIC" | "BUG" | "ROCK" | "GHOST" | "DRAGON" | "DARK" | "STEEL" | "FAIRY" | "NONE";

const colors: Record<Type, string> = {
    NORMAL: "#9FA19F",
    FIRE: "#E62829",
    WATER: "#2980EF",
    GRASS: "#3FA129",
    ELECTRIC: "#FAC000",
    ICE: "#3DCEF3",
    FIGHTING: "#FF8000",
    POISON: "#9141CB",
    GROUND: "#915121",
    FLYING: "#81B9EF",
    PSYCHIC: "#EF4179",
    BUG: "#91A119",
    ROCK: "#AFA981",
    GHOST: "#704170",
    DRAGON: "#5060E1",
    DARK: "#624D4E",
    STEEL: "#60A1B8",
    FAIRY: "#EF70EF",
    NONE: "#68A090"
}
export const Type = {
    toHex(type: Type) {
        return colors[type];
    }
}