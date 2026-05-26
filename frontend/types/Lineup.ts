export type Lineup<T> = {
    first: T[],
    second: T[],
    third: T[]
}

export const Lineup = {
    fromArray<T>(data: T[][]): Lineup<T> {
        if (data.length !== 3) {
            throw new Error("Input array must have 3 elements for first, second, and third lineups.");
        }
        return {
            first: data[0],
            second: data[1],
            third: data[2],
        };
    },
    
    toArray<T>(lineup: Lineup<T>): T[][] {
        return [lineup.first, lineup.second, lineup.third];
    }
};
