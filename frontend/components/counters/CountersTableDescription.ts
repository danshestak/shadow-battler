export type CountersTableDescription = {
    dropdownIndicator?: boolean,
    species?: boolean,
    moves?: boolean,
    time?: boolean,
    winpercent?: boolean,
    score?: boolean
}

export function getGridColsByDesc(desc:CountersTableDescription) {
    return [
        desc.dropdownIndicator ? "minmax(1ch, 0)" : "",
        desc.species ? "minmax(0, 4fr)" : "",
        desc.moves ? "minmax(0, 3fr)" : "",
        desc.time ? "minmax(0, 1fr)" : "",
        desc.winpercent ? "minmax(0, 1fr)" : "",
        desc.score ? "minmax(0, 1fr)" : "",
    ].filter(Boolean).join(" ")
}