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
        desc.dropdownIndicator ? "max-content" : "",
        desc.species ? "minmax(0, 3.5fr)" : "",
        desc.moves ? `minmax(0, ${3 + (desc.time ? 0.5 : 0) + (desc.winpercent ? 0.5 : 0) }fr)` : "",
        desc.time ? "minmax(max-content, 1fr)" : "",
        desc.winpercent ? "minmax(max-content, 1fr)" : "",
        desc.score ? "minmax(max-content, 1fr)" : "",
    ].filter(Boolean).join(" ")
}